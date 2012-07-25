/*
 * Copyright 2012 by lichtflut Forschungs- und Entwicklungsgesellschaft mbH
 */
package org.arastreju.bindings.rdb;

import org.arastreju.bindings.rdb.tx.JdbcTxProvider;
import org.arastreju.sge.persistence.TxProvider;
import org.arastreju.sge.spi.abstracts.AbstractConversationContext;

/**
 * <p>
 *  RRdb specific extension of AbstractConversationContext. 
 * </p>
 *
 * <p>
 * 	Created 23.07.2012
 * </p>
 *
 * @author Raphael Esterle
 */
public class RdbConversationContext extends AbstractConversationContext {
	
	private RdbConnectionProvider connectionProvider;
	private String table;
	private Cache cache;
	
	// ----------------------------------------------------
	
	public RdbConversationContext(RdbConnectionProvider connectionProvider, String table) {
		this.connectionProvider = connectionProvider;
		this.table=table;
		cache = new Cache();
	}
	
	// ----------------------------------------------------
	
	@Override
	public TxProvider getTxProvider() {
		return new JdbcTxProvider();
	}

	@Override
	protected void clearCaches() {
	}
	
	public RdbConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}
	
	public String getTable(){
		return table;
	}
	
	public Cache getCache(){
		return cache;
	}
}
