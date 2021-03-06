/*
 * Copyright (C) 2012 lichtflut Forschungs- und Entwicklungsgesellschaft mbH
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

import org.arastreju.sge.apriori.RDF;
import org.arastreju.sge.model.ResourceID;
import org.arastreju.sge.model.Statement;
import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.model.nodes.SemanticNode;
import org.arastreju.sge.naming.QualifiedName;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 *  View on all entities (instances of classes).
 * </p>
 * 
 * Created: 12.09.2008
 * 
 * @author Oliver Tigges
 */
public class SNEntity extends ResourceView {

    public static SNEntity from(SemanticNode node) {
        if (node instanceof SNEntity) {
            return (SNEntity) node;
        } else if (node instanceof ResourceNode) {
            return new SNEntity((ResourceNode) node);
        } else {
            return null;
        }
    }

    // ----------------------------------------------------
	
	public SNEntity() {
		super();
	}
	
	public SNEntity(final QualifiedName qn) {
		super(qn);
	}
	
	/**
	 * Create a new entity views on given resource.
	 * @param resource The resource to be wrapped by views.
	 */
	public SNEntity(final ResourceNode resource) {
		super(resource);
	}

	//------------------------------------------------------

	/**
	 * Get all direct classes, i.e. with asserted rdf:type, not inferred.
	 * @return A set with the direct classes.
	 */
	public Set<SNClass> getDirectClasses() {
		Set<SNClass> result = new HashSet<SNClass>();
		for(Statement assoc: getAssociations(RDF.TYPE)){
			result.add(SNClass.from(assoc.getObject()));
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
	
	public boolean isInstanceOf(final ResourceID type) {
		for (SNClass clazz : getDirectClasses()) {
			if (clazz.isSpecializationOf(type)){
				return true;
			}
		}
		return false;
	}

}