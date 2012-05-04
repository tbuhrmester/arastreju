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
package org.arastreju.sge.security.impl;

import org.arastreju.sge.apriori.Aras;
import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.model.nodes.views.ResourceView;
import org.arastreju.sge.security.Permission;

/**
 * <p>
 *  Implementation of a {@link Permission}.
 * </p>
 *
 * <p>
 * 	Created Sep 2, 2011
 * </p>
 *
 * @author Oliver Tigges
 */
public class SNPermission extends ResourceView implements Permission {
	
	/**
	 * Constructor.
	 * @param node The node representing this permission.
	 */
	public SNPermission(final ResourceNode node) {
		super(node);
	}
	
	// -----------------------------------------------------

	/**
	 * {@inheritDoc} 
	 */
	public ResourceNode getAssociatedResource() {
		return getResource();
	}
	

	/**
	 * {@inheritDoc} 
	 */
	public String getName() {
		return stringValue(Aras.HAS_UNIQUE_NAME);
	}
	
}