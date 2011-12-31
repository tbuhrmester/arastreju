/*
 * Copyright (C) 2011 lichtflut Forschungs- und Entwicklungsgesellschaft mbH
 */
package org.arastreju.sge.query;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.arastreju.sge.model.nodes.ResourceNode;

/**
 * <p>
 *  Result of a {@link Query}.
 * </p>
 *
 * <p>
 * 	Created Nov 4, 2011
 * </p>
 *
 * @author Oliver Tigges
 */
public class SimpleQueryResult implements QueryResult {
	
	public static QueryResult EMPTY = new SimpleQueryResult(Collections.<ResourceNode>emptyList());

	private List<ResourceNode> list;
	
	// -----------------------------------------------------
	
	/**
	 * Constructor based on a list.
	 */
	public SimpleQueryResult(final List<ResourceNode> list) {
		this.list = list;
	}
	
	// -----------------------------------------------------
	
	/** 
	 * {@inheritDoc}
	 */
	public int size() {
		return list.size();
	};
	
	/** 
	 * {@inheritDoc}
	 */
	public List<ResourceNode> toList() {
		return list;
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public Iterator<ResourceNode> iterator() {
		return list.iterator();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	/** 
	* {@inheritDoc}
	*/
	public ResourceNode getSingleNode() {
		if (list.isEmpty()) {
			return null;
		} else if (list.size() > 1) {
			throw new IllegalStateException("More than one result found.");
		} else {
			return list.get(0);
		}
	}
	
	/** 
	 * {@inheritDoc}
	 */
	public void close() {
		// do nothing.
	}
	
}
