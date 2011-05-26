/*
 * Copyright (C) 2010 lichtflut Forschungs- und Entwicklungsgesellschaft mbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arastreju.bindings.neo4j.impl;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.lucene.search.Query;
import org.arastreju.bindings.neo4j.ArasRelTypes;
import org.arastreju.bindings.neo4j.NeoConstants;
import org.arastreju.bindings.neo4j.extensions.NeoAssociationKeeper;
import org.arastreju.bindings.neo4j.extensions.SNResourceNeo;
import org.arastreju.bindings.neo4j.index.ResourceIndexDumper;
import org.arastreju.bindings.neo4j.mapping.NodeMapper;
import org.arastreju.sge.context.Context;
import org.arastreju.sge.model.ResourceID;
import org.arastreju.sge.model.SemanticGraph;
import org.arastreju.sge.model.associations.Association;
import org.arastreju.sge.model.associations.AssociationKeeper;
import org.arastreju.sge.model.associations.DetachedAssociationKeeper;
import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.model.nodes.SemanticNode;
import org.arastreju.sge.model.nodes.ValueNode;
import org.arastreju.sge.model.nodes.views.SNContext;
import org.arastreju.sge.naming.QualifiedName;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.index.IndexService;
import org.neo4j.index.lucene.LuceneFulltextIndexService;
import org.neo4j.index.lucene.LuceneFulltextQueryIndexService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.lichtflut.infra.exceptions.NotYetImplementedException;

/**
 * <p>
 *  The Neo4jDataStore consists of three data containers:
 *  <ul>
 *  	<li>The Graph Database Service, containing the actual graph</li>
 *  	<li>An Index Service, mapping URLs and keywords to nodes</li>
 *  	<li>A Registry mapping QualifiedNames to Arastreju Resources</li>
 *  </ul>
 * </p>
 *
 * <p>
 * 	Created Sep 2, 2010
 * </p>
 *
 * @author Oliver Tigges
 */
public class NeoDataStore implements NeoConstants, ResourceResolver {
	
	private final GraphDatabaseService gdbService;
	
	private final LuceneFulltextIndexService indexService;
	
	private final NodeMapper mapper;
	
	private final ResourceRegistry registry = new ResourceRegistry();
	
	private final Logger logger = LoggerFactory.getLogger(NeoDataStore.class);

	private final ResourceIndexDumper iDumper;
	
	// -----------------------------------------------------

	/**
	 * Default constructor. Will use a <b>temporary</b> datastore!.
	 */
	public NeoDataStore() throws IOException {
		this(prepareTempStore());
	}
	
	/**
	 * Constructor. Creates a store using given directory.
	 * @param dir The directory for the store.
	 */
	public NeoDataStore(final String dir) {
		logger.info("Neo4jDataStore created in " + dir);
		iDumper = new ResourceIndexDumper(dir + "/index" );
		gdbService = new EmbeddedGraphDatabase(dir); 
		indexService = new LuceneFulltextQueryIndexService(gdbService){
			protected Query formQuery(String key, Object value, Object matching){
				String val = value.toString();
				String[] SPECIAL_CHARACTERS = new String[]{
						"+","-","&&","||","!","(",")","{","}","[","]","^","~","?",":"	
				};
				for (int i = 0; i < SPECIAL_CHARACTERS.length; i++) {
				  val = val.replace(SPECIAL_CHARACTERS[i], "\\" + SPECIAL_CHARACTERS[i]);
				}
				return super.formQuery(key, val, matching);
			}
		};
		//indexService.enableCache(INDEX_KEY_RESOURCE_URI, CACHE_SIZE);
		mapper = new NodeMapper(this);
	}

	// -----------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public ResourceNode findResource(final QualifiedName qn) {
		if (registry.contains(qn)){
			return registry.get(qn);
		}
		// if not yet registered, load and wrap
		return doTransacted(new TxResultAction<ResourceNode>() {
			public ResourceNode execute(NeoDataStore store) {
				final Node neoNode = indexService.getSingleNode(INDEX_KEY_RESOURCE_URI, qn.toURI());
				logger.debug("IndexLookup: " + qn + " --> " + neoNode); 
				if (neoNode != null){
					final SNResourceNeo arasNode = new SNResourceNeo(qn);
					registry.register(arasNode);
					mapper.toArasNode(neoNode, arasNode);
					return arasNode;
				} else {
					return null;
				}
			}
		});
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public ResourceNode findResource(final Node neoNode) {
		final QualifiedName qn = new QualifiedName(neoNode.getProperty(PROPERTY_URI).toString());
		if (registry.contains(qn)){
			return registry.get(qn);
		}
		final SNResourceNeo arasNode = new SNResourceNeo(qn);
		registry.register(arasNode);
		mapper.toArasNode(neoNode, arasNode);
		return arasNode;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public ResourceNode resolve(final ResourceID resource) {
		if (resource.isAttached()){
			return resource.asResource();
		} else {
			ResourceNode node = findResource(resource.getQualifiedName());
			if (node == null){
				return doTransacted(new TxResultAction<ResourceNode>() {
					public ResourceNode execute(NeoDataStore store) {
						return persist(resource.asResource());
					}
				});
			}
			return node;
		}
	}
	
	/**
	 * Attach the given node if it is not already attached.
	 * @param resource The node to attach.
	 * @return A node attached by guaranty.
	 */
	public ResourceNode attach(final ResourceNode resource) {
		// 1st: check if node is already attached.
		if (resource.isAttached()){
			return resource;
		}
		return doTransacted(new TxResultAction<ResourceNode>() {
			public ResourceNode execute(NeoDataStore store) {
				// 2nd: check if node for qualified name exists and has to be merged
				ResourceNode attached = findResource(resource.getQualifiedName());
				if (attached != null){
					attached = merge(attached, resource);
				} else {
					// 3rd: if resource is really new, create a new Neo node.
					attached = persist(resource);
				}
				return attached;
			}
		});
	}
	
	/**
	 * Unregister the node from the registry and detach the {@link AssociationKeeper}
	 * @param node
	 */
	public void detach(final ResourceNode node){
		registry.unregister(node);
		AssocKeeperAccess.setAssociationKeeper(node, new DetachedAssociationKeeper(node.getAssociations()));
	}
	
	// -----------------------------------------------------

	/**
	 * Returns a {@link ResourceIndexDumper} to dump out the current index of this store
	 */
	public ResourceIndexDumper getIndexDumper(){
		return iDumper;
	}
	
	// -----------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public SemanticGraph attach(final SemanticGraph graph){
		return doTransacted(new TxResultAction<SemanticGraph>() {
			public SemanticGraph execute(NeoDataStore store) {
				for(ResourceNode node : graph.getSubjects()){
					attach(node);
				}
				return graph;
			}
		});
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void detach(final SemanticGraph graph){
		for(SemanticNode node : graph.getNodes()){
			if (node.isAttached() && node.isResourceNode()){
				detach(node.asResource());
			}
		}
	}
	
	// -----------------------------------------------------
	
	/**
	 * Close the graph database;
	 */
	public void close() {
		gdbService.shutdown();
	}

	/**
	 * Add a new Association to given Neo node, or rather create a corresponding Relation.
	 * @param subject The neo node, which shall be the subject in the new Relation.
	 * @param assoc The Association.
	 */
	public void addAssociation(final Node subject, final Association assoc) {
		doTransacted(new TxAction() {
			public void execute(NeoDataStore store) {
				final SemanticNode client = assoc.getObject();
				final ResourceNode predicate = resolve(assoc.getPredicate());
				
				Context ctx = assoc.getContext();
				if (ctx != null){
					ctx = new SNContext(resolve(ctx));
				}
				
				if (client.isResourceNode()){
					// Resource node
					final ResourceNode arasClient = resolve(client.asResource());
					final Node neoClient = AssocKeeperAccess.getNeoNode(arasClient);
					
					final Relationship relationship = subject.createRelationshipTo(neoClient, ArasRelTypes.REFERENCE);
					relationship.setProperty(PREDICATE_URI, predicate.getQualifiedName().toURI());
					if (ctx != null){
						relationship.setProperty(CONTEXT_URI, ctx.asResource().getQualifiedName().toURI());	
					}
					indexService.index(subject, predicate.getQualifiedName().toURI(), arasClient.getQualifiedName().toURI());
					logger.debug("added relationship--> " + relationship + " to node " + subject);
				} else {
					// Value node
					final Node neoClient = gdbService.createNode();
					final ValueNode value = client.asValue();
					neoClient.setProperty(PROPERTY_VALUE, value.getValue().toString());
					neoClient.setProperty(PROPERTY_DATATYPE, client.asValue().getDataType().name());
					
					final Relationship relationship = subject.createRelationshipTo(neoClient, ArasRelTypes.VALUE);
					relationship.setProperty(PREDICATE_URI, predicate.getQualifiedName().toURI());
					if (ctx != null){
						relationship.setProperty(CONTEXT_URI, ctx.asResource().getQualifiedName().toURI());	
					}
					
					logger.debug("added value --> " + relationship + " to node " + subject);

					indexService.index(subject, INDEX_KEY_RESOURCE_VALUE, value.getStringValue());
					indexService.index(subject, predicate.getQualifiedName().toURI(), value.getStringValue());
					logger.debug("Indexed: " + value.getStringValue() + " --> " + subject);
				}
			}
		});
	}
	
	// -----------------------------------------------------
	
	public void doTransacted(final TxAction action){
		Transaction tx = gdbService.beginTx();
		try {
			action.execute(this);
			tx.success();
		} finally {
			tx.finish();
		}
	}
	
	public <T> T doTransacted(final TxResultAction<T> action){
		Transaction tx = gdbService.beginTx();
		try {
			T result = action.execute(this);
			tx.success();
			return result;
		} finally {
			tx.finish();
		}
	}
	
	// -----------------------------------------------------
	
	/**
	 * Merges all associations from the 'changed' node to the 'attached' node.
	 * @param attached The currently attached node.
	 * @param changed An unattached node referencing the same resource.
	 * @return The merged {@link ResourceNode}.
	 */
	public ResourceNode merge(final ResourceNode attached, final ResourceNode changed) {
		final AssociationKeeper ak = AssocKeeperAccess.getAssociationKeeper(changed);
		if (!ak.getRevokedAssociations().isEmpty()){
			throw new NotYetImplementedException("Revoked Assocs cannot be merged yet.");
		}
		final Set<Association> currentAssocs = attached.getAssociations();
		for(Association assoc : ak.getAssociations()){
			if (!currentAssocs.contains(assoc)){
				Association.create(attached, assoc.getPredicate(), assoc.getObject(), assoc.getContexts());
			}
		}
		return attached;
	}
	
	public IndexService getIndexService() {
		return indexService;
	}
	
	public GraphDatabaseService getGdbService() {
		return gdbService;
	}
	
	public ResourceRegistry getRegistry() {
		return registry;
	}
	
	// -----------------------------------------------------
	
	/**
	 * Create the given resource node in Neo4j DB.
	 * @param node A not yet persisted node.
	 * @return The persisted ResourceNode.
	 */
	protected ResourceNode persist(final ResourceNode node) {
		// 1st: create a corresponding Neo node.
		final Node neoNode = gdbService.createNode();
		mapper.toNeoNode(node, neoNode);
		
		// 2nd: index the Neo node.
		final QualifiedName qn = node.getQualifiedName();
		indexService.index(neoNode, INDEX_KEY_RESOURCE_URI, qn.toURI());
		logger.debug("Indexed: " + qn + " --> " + neoNode);
		
		// 3rd: attach the Resource with this store.
		final Set<Association> copy = node.getAssociations();
		AssocKeeperAccess.setAssociationKeeper(node, new NeoAssociationKeeper(node, neoNode, this));
		
		// 4th: register resource.
		registry.register(node);
		
		// 5th: store all associations.
		for (Association assoc : copy) {
			addAssociation(neoNode, assoc);
		}
		
		return node;
	}
	
	// -----------------------------------------------------
	
	private static String prepareTempStore() throws IOException {
		final File temp = File.createTempFile("aras", Long.toString(System.nanoTime()));
		if (!temp.delete()) {
			throw new IOException("Could not delete temp file: "
					+ temp.getAbsolutePath());
		}
		if (!temp.mkdir()) {
			throw new IOException("Could not create temp directory: "
					+ temp.getAbsolutePath());
		}
		
		return temp.getAbsolutePath();
	}

}
