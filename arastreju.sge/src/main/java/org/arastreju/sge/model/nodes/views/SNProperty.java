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
package org.arastreju.sge.model.nodes.views;

import java.util.HashSet;
import java.util.Set;

import org.arastreju.sge.apriori.Owl;
import org.arastreju.sge.apriori.RDF;
import org.arastreju.sge.apriori.RDFS;
import org.arastreju.sge.model.ResourceID;
import org.arastreju.sge.model.associations.Association;
import org.arastreju.sge.model.nodes.SNResource;
import org.arastreju.sge.model.nodes.SemanticNode;

/**
 * <p>
 * 	Resource view for Properties.
 * 	Corresponds to rdfs:Property.
 * </p>
 * 
 * <p>
 * 	Created: 10.06.2009
 * </p>
 *
 * @author Oliver Tigges 
 */
public class SNProperty extends ResourceView {

	private Set<SNProperty> inverseProperties;
	
	// ------------------------------------------------------
	
	/**
	 * Creates a new Property view for given resource.
	 */
	public SNProperty(final SNResource resource) {
		super(resource);
	}

	/**
	 * @param stereotype
	 */
	public SNProperty() {
	}

	// ------------------------------------------------------
	
	/**
	 * Collects all super properties up the inheritance hierarchy, expressed by rdfs:subPropertyOf.
	 * @return A set with all super properties.
	 */
	public Set<SNProperty> getSuperProperties() {
		final Set<SNProperty> superProperties = new HashSet<SNProperty>();
		superProperties.add(this);
		final Set<Association> extensions = getAssociations(RDFS.SUB_PROPERTY_OF);
		for (Association current : extensions) {
			final SNProperty directSuperProperty = current.getClient().asResource().asProperty();
			superProperties.add(directSuperProperty);
			superProperties.addAll(directSuperProperty.getSuperProperties());
		}
		return superProperties;
	}
	
	/**
	 * Collects all super properties up the inheritance hierarchy, expressed by rdfs:subPropertyOf.
	 * @return A set with all super properties.
	 */
	public Set<SNProperty> getDirectSuperProperties() {
		final Set<SNProperty> result = new HashSet<SNProperty>();
		final Set<Association> extensions = getAssociations(RDFS.SUB_PROPERTY_OF);
		for (Association current : extensions) {
			final SNProperty directSuperProperty = current.getClient().asResource().asProperty();
			result.add(directSuperProperty);
		}
		return result;
	}
	
	/**
	 * Collects all properties that are declared to be inverse of this, expressed by owl:inverseOf.
	 * @return A set with all super properties.
	 */
	public Set<SNProperty> getInverseProperties() {
		if (inverseProperties == null){
			inverseProperties = new HashSet<SNProperty>();
			Set<Association> assocs = getAssociations(Owl.INVERSE_OF);
			for (Association current : assocs) {
				SNProperty property = current.getClient().asResource().asProperty();
				inverseProperties.add(property);
			}
		}
		return inverseProperties;
	}
	
	/**
	 * Checks if this property is of given resource.
	 * @param ref The resource reference to be checked.
	 * @return true if this property is an instance of given resource.
	 */
	public boolean isSubPropertyOf(final ResourceID ref){
		return getSuperProperties().contains(ref);
	}
	
	// ------------------------------------------------------
	
	/**
	 * Reveals if this property is symmetric, i.e. it extends owl:SymmetricProperty.
	 * To symmetric properties applies: A p B -> B p A.
	 */
	public boolean isSymmetric(){
		return isOfType(Owl.SYMMETRIC_PROPERTY);
	}
	
	/**
	 * Reveals if this property is transitive, i.e. it extends owl:TransitiveProperty.
	 * To transitive properties applies: A p B & B p C -> A p C.
	 */
	public boolean isTransitive(){
		return isOfType(Owl.TRANSITIVE_PROPERTY);
	}
	
	/**
	 * Reveals if this property is functional, i.e. it extends owl:FuntionalProperty.
	 * If this property is inverse functional it is a unique identifier the objects.
	 */
	public boolean isFunctional(){
		return isOfType(Owl.FUNCTIONAL_PROPERTY);
	}
	
	/**
	 * Reveals if this property is inverse functional, i.e. it extends owl:InverseFuntionalProperty.
	 * If this property is inverse functional it is a unique identifier the subjects.
	 */
	public boolean isInverseFunctional(){
		return isOfType(Owl.INVERSE_FUNCTIONAL_PROPERTY);
	}
	
	// ------------------------------------------------------
	
	/**
	 * Checks if this property has a rdf:type association to given type.
	 */
	protected boolean isOfType(final ResourceID type){
		Set<Association> assocs = getAssociations(RDF.TYPE);
		for (Association association : assocs) {
			final SemanticNode client = association.getClient();
			if (client.isResourceNode() && client.asResource().references(type)){
				return true;
			}
		}
		return false;
	}
	
}