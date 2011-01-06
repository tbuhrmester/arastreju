/*
 * Copyright (C) 2009 lichtflut Forschungs- und Entwicklungsgesellschaft mbH
 */
package org.arastreju.bindings.neo4j.mapping;

import org.arastreju.bindings.neo4j.NeoConstants;
import org.arastreju.bindings.neo4j.extensions.NeoAssociationKeeper;
import org.arastreju.bindings.neo4j.extensions.SNResourceNeo;
import org.arastreju.bindings.neo4j.extensions.SNValueNeo;
import org.arastreju.bindings.neo4j.impl.NeoDataStore;
import org.arastreju.sge.model.associations.Association;
import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.model.nodes.SNResource;
import org.arastreju.sge.model.nodes.SNValue;
import org.arastreju.sge.naming.QualifiedName;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * <p>
 *  [DESCRIPTION]
 * </p>
 *
 * <p>
 * 	Created Sep 2, 2010
 * </p>
 *
 * @author Oliver Tigges
 */
public class NodeMapper implements NeoConstants {
	
	private final NeoDataStore store;

	/**
	 * Default constructor.
	 * @param neo4jDataStore 
	 */
	public NodeMapper(NeoDataStore neo4jDataStore) {
		this.store = neo4jDataStore;
	}
	
	// -----------------------------------------------------

	public void toNeoNode(final ResourceNode arasNode, final Node neoNode){
		neoNode.setProperty(PROPERTY_URI, arasNode.getQualifiedName().toURI());
	}
	
	public SNResource toArasNode(final Node neoNode){
		final String uri = (String) neoNode.getProperty(NeoConstants.PROPERTY_URI);
		final QualifiedName qn = new QualifiedName(uri);
		
		final NeoAssociationKeeper assocKeeper = new NeoAssociationKeeper(neoNode, store);
		
		final SNResourceNeo arasNode = 
			new SNResourceNeo(qn, assocKeeper);
		
		for(Relationship rel : neoNode.getRelationships(Direction.OUTGOING)){
			final Node neoClient = rel.getEndNode();
			if (neoClient.hasProperty(PROPERTY_URI)){
				// Resource Relation
				ResourceNode object = store.findResource(rel.getEndNode());
				ResourceNode predicate = store.findResource(new QualifiedName(rel.getProperty(PROPERTY_URI).toString()));
				Association.create(arasNode, predicate, object, null);
			} else if (neoClient.hasProperty(PROPERTY_VALUE)){
				// Value assignment
				final SNValue value = new SNValueNeo(neoClient);
				ResourceNode predicate = store.findResource(new QualifiedName(rel.getProperty(PROPERTY_URI).toString()));
				Association.create(arasNode, predicate, value, null);
			} else {
				throw new IllegalStateException("Relation end has neither URI nor Value");
			}
		}
		
		return arasNode;
	}

}
