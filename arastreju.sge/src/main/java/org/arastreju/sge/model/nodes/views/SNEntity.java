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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.arastreju.sge.apriori.Aras;
import org.arastreju.sge.apriori.RDF;
import org.arastreju.sge.context.Context;
import org.arastreju.sge.model.ResourceID;
import org.arastreju.sge.model.associations.Association;
import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.model.nodes.SNResource;
import org.arastreju.sge.model.nodes.SemanticNode;

import de.lichtflut.infra.logging.Log;

/**
 * <p>
 * View on all entities (instances of classifiers).
 * </p>
 * 
 * Created: 12.09.2008
 * 
 * @author Oliver Tigges
 */
public class SNEntity extends ResourceView {
	
	public static boolean isEntity(SemanticNode node) {
		return node instanceof SNEntity;
	}
	
	//-----------------------------------------------------
	
	public SNEntity() {
		super(new SNResource());
	}
	
	/**
	 * Create a new entity view on given resource.
	 * @param resource The resource to be wrapped by view.
	 */
	public SNEntity(final ResourceNode resource) {
		super(resource);
	}

	//------------------------------------------------------

	/**
	 * Get the 'main' class of this entity.
	 */
	public SNClass getMainClass() {
		Association assoc = getSingleAssociation(RDF.TYPE);
		if (assoc != null){
			return assoc.getClient().asResource().asClass();
		}
		Log.warn(this, "individual has no class: " + this);
		return null;
	}
	
	/**
	 * Get all direct classes, i.e. with asserted rdf:type, not inferred.
	 * @return A set with the direct classes.
	 */
	public Set<SNClass> getDirectClasses() {
		Set<SNClass> result = new HashSet<SNClass>();
		for(Association assoc: getAssociations(RDF.TYPE)){
			result.add(assoc.getClient().asResource().asClass());
		}
		return result;
	}
	
	/**
	 * Check if this entity is member of at least one class.
	 * @return true, if this entity has a class.
	 */
	public boolean hasClass(){
		return !getAssociations(RDF.TYPE).isEmpty();
	}
	
	public synchronized void addToClass(SNClass newClass, Context context) {
		Association.create(this, RDF.TYPE, newClass, context);
	}

	public boolean isInstanceOf(final ResourceID classifier) {
		for (SNClass clazz : getDirectClasses()) {
			if (clazz.isSpecializationOf(classifier)){
				return true;
			}
		}
		return false;
	}

	public Set<Association> getRelations(final ResourceID role) {
		return getAssociations(role);
	}
	
	//-----------------------------------------------------
	
	public boolean isNamed() {
		return !getAssociations(Aras.HAS_PROPER_NAME).isEmpty();
	}
	
	public List<SNName> getNames(){
		List<SNName> result = new ArrayList<SNName>();
		Set<Association> assocs = getAssociations(Aras.HAS_PROPER_NAME);
		for (Association current : assocs) {
			result.add(current.getClient().asValue().asName());
		}
		return result;
	}

	public void addName(SNName name, ResourceID type, Context context) {
		Association.create(this, type, name, context);
	}
	
}