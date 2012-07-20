package org.arastreju.sge.spi.abstracts;

import org.arastreju.sge.ConversationContext;
import org.arastreju.sge.ModelingConversation;
import org.arastreju.sge.SNOPS;
import org.arastreju.sge.model.SemanticGraph;
import org.arastreju.sge.model.Statement;
import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.model.nodes.SemanticNode;
import org.arastreju.sge.persistence.TransactionControl;
import org.arastreju.sge.persistence.TxResultAction;

/**
 * <p>
 * Abstract base for modeling conversations.
 * </p>
 *
 * <p>
 *  Created 06.07.12
 * </p>
 *
 * @author Oliver Tigges
 */
public abstract class AbstractModelingConversation implements ModelingConversation {

    private ConversationContext conversationContext;

    // ----------------------------------------------------
    
    public AbstractModelingConversation(ConversationContext conversationContext) {
		this.conversationContext = conversationContext;
	}
    
    /**
     * @deprecated Use other consgtructor with conversation.
     */
    public AbstractModelingConversation() {
		this.conversationContext = null;
	}
    
    // ----------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void addStatement(final Statement stmt) {
        assertActive();
        final ResourceNode subject = resolve(stmt.getSubject());
        SNOPS.associate(subject, stmt.getPredicate(), stmt.getObject(), stmt.getContexts());
    }

	/**
    * {@inheritDoc}
    */
    @Override
    public boolean removeStatement(final Statement stmt) {
        assertActive();
        final ResourceNode subject = resolve(stmt.getSubject());
        return SNOPS.remove(subject, stmt.getPredicate(), stmt.getObject());
    }
    

	/**
	 * {@inheritDoc}
	 */
	public void attach(final SemanticGraph graph) {
		assertActive();
		conversationContext.getTxProvider().doTransacted(new TxResultAction<SemanticGraph>() {
			public SemanticGraph execute() {
				for(Statement stmt : graph.getStatements()) {
					final ResourceNode subject = resolve(stmt.getSubject());
					SNOPS.associate(subject, stmt.getPredicate(), stmt.getObject(), stmt.getContexts());
				}
				return graph;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void detach(final SemanticGraph graph) {
		assertActive();
		for(SemanticNode node : graph.getNodes()){
			if (node.isResourceNode() && node.asResource().isAttached()){
				detach(node.asResource());
			}
		}
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ConversationContext getConversationContext() {
    	return conversationContext;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    	conversationContext.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionControl beginTransaction() {
    	return conversationContext.getTxProvider().begin();
    }

    // ----------------------------------------------------

    protected abstract void assertActive();
}
