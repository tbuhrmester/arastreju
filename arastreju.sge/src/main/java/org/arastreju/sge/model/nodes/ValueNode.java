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
package org.arastreju.sge.model.nodes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

import org.arastreju.sge.model.ElementaryDataType;
import org.arastreju.sge.model.nodes.views.SNScalar;
import org.arastreju.sge.model.nodes.views.SNText;
import org.arastreju.sge.model.nodes.views.SNTimeSpec;

/**
 * <p>
 *  Base interface for all value nodes.
 * </p>
 *
 * <p>
 * 	Created Jan 5, 2010
 * </p>
 *
 * @author Oliver Tigges
 */
public interface ValueNode extends SemanticNode, Comparable<ValueNode> {

	ElementaryDataType getDataType();

	Object getValue();
	
	Locale getLocale();
	
	// ------------------------------------------------------

	/**
	 * @return The string value.
	 */
	String getStringValue();
	
	/**
	 * @return The boolean value.
	 */
	Boolean getBooleanValue();
	
	/**
	 * @return The integer value.
	 */
	BigInteger getIntegerValue();
	
	/**
	 * @return The decimal value.
	 */
	BigDecimal getDecimalValue();
	
	/**
	 * @return The date value.
	 */
	Date getTimeValue();
	
	// ------------------------------------------------------
	
	SNTimeSpec asTimeSpec();

	SNScalar asScalar();

	SNText asText();

}