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
package org.arastreju.sge;

import org.arastreju.sge.model.ResourceID;
import org.arastreju.sge.model.SemanticGraph;
import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.naming.QualifiedName;
import org.arastreju.sge.persistence.TransactionControl;
import org.arastreju.sge.query.QueryManager;
import org.arastreju.sge.settings.ConversationSettings;

/**
 * <p>
 *  A Modeling Conversation allows to read an change parts of the semantic model.
 * </p>
 *
 * <p>
 * 	Created Sep 1, 2010
 * </p>
 *
 * @author Oliver Tigges
 */
public interface ModelingConversation {
	
	// -- RESOURCE NODE -----------------------------------
	
	/**
	 * Tries to find an existing resource with given qualified name. The returned node will be attached.
	 * If no resource is found null will be returned. 
	 * @param qn The qualified name.
	 * @return The corresponding resource or <code>null</code>.
	 */
	ResourceNode findResource(QualifiedName qn);
	
	/**
	 * Resolves the resource referenced by given resource identifier. The returned node will be attached.
	 * If there does not exist a corresponding resource, it will be created.
	 * Thus there will never be returned null. 
	 * @param resourceID The resource identifier.
	 * @return The corresponding resource node.
	 */
	ResourceNode resolve(ResourceID resourceID);
	
	/**
	 * Attach the resource to the conversation. 
	 * All changes to the attached node will immediately be persistent.
	 * @param node The node to be attached.
	 * @return The attached node.
	 */
	ResourceNode attach(ResourceNode node);
	
	/**
	 * Detach the given node from the conversation.
	 * All following changes will not affect the underlying store.
	 * @param node The node to be detached.
	 */
	void detach(ResourceNode node);
	
	// -- SEMANTIC GRAPH ----------------------------------
	
	SemanticGraph findGraph(QualifiedName qn);
	
	SemanticGraph attach(SemanticGraph graph);
	
	void detach(SemanticGraph graph);
	
	// -----------------------------------------------------
	
	QueryManager createQueryManager();
	
	// -----------------------------------------------------
	
	ConversationSettings getSettings();
	
	TransactionControl getTransactionControl();
	
	/**
	 * Finish the conversation and release all resources.
	 */
	void close();

}
