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
package org.arastreju.sge.persistence;

/**
 * <p>
 * 	Interface to transaction controlling of Arastreju gate.
 * </p>
 * 
 * <p>
 * 	Created: 10.07.2008
 * </p>
 *
 * @author Oliver Tigges
 */
public interface TransactionControl {
	
	/**
	 * Marks the transaction as successful. 
	 */
	void success();
	
	/**
	 * Marks the transaction as failed. 
	 */
	void fail();
	
	/**
	 * Finish the transaction. If fail() has been called during this transaction a rollback will be performed. 
	 * Otherwise the transaction will be committed.
	 */
	void finish();
	
	/**
	 * Commit the transaction.
	 */
	void commit();
	
	/**
	 * Roles the transaction back .
	 */
	void rollback();
	
	/**
	 * flushes current state to database.
	 */
	void flush();

    /**
     * Check if the current transaction is active.
     * @return true if there is an active transaction
     */
    boolean isActive();

	
	
}
