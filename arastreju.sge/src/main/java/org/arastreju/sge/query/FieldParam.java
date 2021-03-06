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

import org.arastreju.sge.model.ResourceID;
import org.arastreju.sge.naming.QualifiedName;

/**
 * <p>
 *  Parameter for a key-value field.
 * </p>
 *
 * <p>
 * 	Created Nov 4, 2011
 * </p>
 *
 * @author Oliver Tigges
 */
public class FieldParam implements QueryParam {

	private final String name;
	
	private final Object value;
	
	// -----------------------------------------------------

	/**
	 * Constructor.
	 * @param name
	 * @param value
	 */
	public FieldParam(final String name, final Object value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Constructor.
	 * @param name
	 * @param value
	 */
	public FieldParam(final ResourceID name, final Object value) {
		this(name.getQualifiedName(), value);
	}
	
	/**
	 * @param name
	 * @param value
	 */
	public FieldParam(final QualifiedName name, final Object value) {
		this(name.toURI(), value);
	}

	// -----------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public QueryOperator getOperator() {
		return QueryOperator.EQUALS;
	};
	
	/** 
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/** 
	 * {@inheritDoc}
	 */
	public Object getValue() {
		return value;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name + "=" + value;
	}
	
}
