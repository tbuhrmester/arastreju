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
package org.arastreju.sge.model;

import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.model.nodes.SNResource;
import org.arastreju.sge.model.nodes.ValueNode;
import org.arastreju.sge.naming.Namespace;
import org.arastreju.sge.naming.QualifiedName;
import org.arastreju.sge.naming.SimpleNamespace;

/**
 * Simple implementation of transient {@link ResourceID}.
 * 
 * Created: 02.02.2009
 *
 * @author Oliver Tigges
 */
public class SimpleResourceID implements ResourceID {
	
	private final QualifiedName qualifiedName;
	
	// -----------------------------------------------------

	/**
	 * Creates a copied ResourceID based on an existing ReosourceID.
	 */
	public SimpleResourceID(final ResourceID id) {
		this(id.getNamespace(), id.getName());
	}
	
	/**
	 * Create a ResourceID based on {@link QualifiedName}.
	 * @param qn The qualified name.
	 */
	public SimpleResourceID(final QualifiedName qn) {
		this(qn.getNamespace(), qn.getSimpleName());
	}
	
	/**
	 * Constructor.
	 * @param nsUri The namespace URI.
	 * @param name The simple name.
	 */
	public SimpleResourceID(final String nsUri, final String name) {
		this(new SimpleNamespace(nsUri), name);
	}
	
	/**
	 * Constructor.
	 * @param namespace The namespace.
	 * @param name The simple name.
	 */
	public SimpleResourceID(final Namespace namespace, final String name) {
		this.qualifiedName = new QualifiedName(namespace, name);
	}
	
	// --- ResourceID -------------------------------------
	
	public QualifiedName getQualifiedName() {
		return qualifiedName;
	}
	
	public boolean references(final ResourceID ref) {
		return qualifiedName.equals(ref.getQualifiedName());
	}
	
	public String getNamespaceUri(){
		return qualifiedName.getNamespace().getUri();
	}
	
	public Namespace getNamespace(){
		return qualifiedName.getNamespace();
	}
	
	public String getName(){
		return qualifiedName.getSimpleName();
	}
	
	// -- INode -------------------------------------------
	
	/* (non-Javadoc)
	 * @see org.arastreju.api.ontology.model.INode#isResourceNode()
	 */
	public boolean isResourceNode() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.arastreju.api.ontology.model.INode#isValueNode()
	 */
	public boolean isValueNode() {
		return false;
	}
	

	/* (non-Javadoc)
	 * @see org.arastreju.api.ontology.model.INode#isAttached()
	 */
	public boolean isAttached() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.arastreju.api.ontology.model.INode#asResource()
	 */
	public ResourceNode asResource() {
		final SNResource sn = new SNResource();
		sn.setName(getName());
		sn.setNamespace(getNamespace());
		return sn;
	}

	/* (non-Javadoc)
	 * @see org.arastreju.api.ontology.model.INode#asValue()
	 */
	public ValueNode asValue() {
		throw new UnsupportedOperationException();
	}

	// -----------------------------------------------------
	
	@Override
	public int hashCode() {
		return qualifiedName.toURI().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ResourceID){
			ResourceID other = (ResourceID) obj;
			return references(other);
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return qualifiedName.toString();
	}
	
}