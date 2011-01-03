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
package org.arastreju.sge.model;

import org.arastreju.sge.apriori.Aras;

import de.lichtflut.infra.exceptions.NotYetSupportedException;

/**
 * Type of value nodes.
 * 
 * Created: 16.01.2009
 *
 * @author Oliver Tigges
 */
public enum ElementaryDataType {

	UNDEFINED,
	
	BOOLEAN,
	INTEGER,
	DECIMAL,
	STRING, 
	URI,
	TIMESTAMP,
	DATE,
	TIME_OF_DAY,
	TERM,
	PROPER_NAME,
	
	RESOURCE;
	
	// ------------------------------------------------------
	
	public static ElementaryDataType typeForClass(final ResourceID node){
		if(node == null){
			return UNDEFINED;
		}
		if (Aras.DATATYPE_STRING.equals(node)){
			return STRING;
		} else if (Aras.DATATYPE_PROPER_NAME.equals(node)){
			return PROPER_NAME;
		} else if (Aras.DATATYPE_TERM.equals(node)){
			return TERM;
		} else if (Aras.DATATYPE_BOOLEAN.equals(node)){
			return BOOLEAN;
		} else if (Aras.DATATYPE_INTEGER.equals(node)){
			return INTEGER;
		} else if (Aras.DATATYPE_DECIMAL.equals(node)){
			return DECIMAL;
		} else if (Aras.DATATYPE_TIMESTAMP.equals(node)){
			return TIMESTAMP;
		} else if (Aras.DATATYPE_DATE.equals(node)){
			return DATE;
		} else if (Aras.DATATYPE_TIME_OF_DAY.equals(node)){
			return TIME_OF_DAY;
		} else if (Aras.DATATYPE_URI.equals(node)){
			return URI;
		} else {
			// default is resource
			return RESOURCE;
		}
	}
	
	public static ResourceID classForType(final ElementaryDataType datatype){
		switch (datatype) {
		case BOOLEAN:
			return Aras.DATATYPE_BOOLEAN;
		case DATE:
			return Aras.DATATYPE_DATE;
		case DECIMAL:
			return Aras.DATATYPE_DECIMAL;
		case INTEGER:
			return Aras.DATATYPE_INTEGER;
		case PROPER_NAME:
			return Aras.DATATYPE_PROPER_NAME;
		case RESOURCE:
			return Aras.DATATYPE_RESOURCE;
		case STRING:
			return Aras.DATATYPE_STRING;
		case TIME_OF_DAY:
			return Aras.DATATYPE_TIME_OF_DAY;
		case TIMESTAMP:
			return Aras.DATATYPE_TIMESTAMP;
		case URI:
			return Aras.DATATYPE_URI;
		case UNDEFINED:
			return null;
		default:
			throw new NotYetSupportedException(datatype);
		}
	}
	
	public static ElementaryDataType getCorresponding(final TimeMask mask){
		switch(mask){
		case DATE:
			return ElementaryDataType.DATE;
		case TIME_OF_DAY:
			return ElementaryDataType.TIME_OF_DAY;
		case TIMESTAMP:
			return ElementaryDataType.TIMESTAMP;
		default:
			throw new IllegalArgumentException("unknown time mask: " + mask);
		}
	}
	
}
