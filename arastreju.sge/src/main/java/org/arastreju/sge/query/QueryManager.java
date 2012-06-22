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
package org.arastreju.sge.query;

import java.util.List;

import org.arastreju.sge.model.ResourceID;
import org.arastreju.sge.model.nodes.ResourceNode;

/**
 * <p>
 *  Manager for queries such as SPARQL and for index lookups.
 * </p>
 *
 * <p>
 * 	Created Sep 2, 2010
 * </p>
 *
 * @author Oliver Tigges
 * @deprecated Will be removed, use conversation.createQuery()
 */
public interface QueryManager {
	
	/**
	 * Find resources with the given rdf:type.
	 * @param type The resource ID of the type.
	 * @return A list with all resources having given resource type as rdf:type.
	 * @deprecated Use a query instead.
	 */
	List<ResourceNode> findByType(ResourceID type);
	
}
