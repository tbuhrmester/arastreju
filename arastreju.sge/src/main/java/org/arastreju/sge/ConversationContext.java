/*
 * Copyright 2012 by lichtflut Forschungs- und Entwicklungsgesellschaft mbH
 */
package org.arastreju.sge;

import org.arastreju.sge.context.Context;

/**
 * <p>
 *  Context of a {@link ModelingConversation}.
 * </p>
 *
 * <p>
 * 	Created Jun 7, 2012
 * </p>
 *
 * @author Oliver Tigges
 */
public interface ConversationContext {

	Context[] getReadContexts();

	Context getWriteContext();
	
	ConversationContext setWriteContext(Context context);
	
	ConversationContext setReadContexts(Context... contexts);
	
	void clear();

}