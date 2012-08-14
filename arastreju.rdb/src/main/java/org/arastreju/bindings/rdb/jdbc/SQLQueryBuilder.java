/*
 * Copyright (C) 2012 lichtflut Forschungs- und Entwicklungsgesellschaft mbH
 */

package org.arastreju.bindings.rdb.jdbc;

/**
 * <p>
 *  Query Builder. 
 * </p>
 *
 * <p>
 * 	Created Feb 1, 2012
 * </p>
 *
 * @author Raphael Esterle
 */

import java.util.Map;

public class SQLQueryBuilder {

	private static final String dim = ",";
	private static final String qm = "'";
	private static final String bo = "(";
	private static final String sp = " ";
	private static final String and = "AND";

	/**
	 * Creates an insert query with the given parameters.
	 * 
	 * @param sub
	 *            Subject
	 * @param pre
	 *            Predicate
	 * @param obj
	 *            Object
	 * @param type
	 *            Type
	 * @return The query as {@link String}
	 */
	public static String createInsert(String table, Map<String, String> columns) {

		StringBuilder query = new StringBuilder("INSERT INTO ");
		StringBuilder subq = new StringBuilder(" VALUES(");
		query.append(table + bo);
		for (String key : columns.keySet()) {
			query.append(key + dim);
			subq.append(qm + columns.get(key) + qm + dim);
		}
		query.setCharAt(query.length() - 1, ')');
		subq.setCharAt(subq.length() - 1, ')');
		subq.append(';');
		query.append(subq.toString());
		return query.toString();
	}

	/**
	 * Creates an select query with the given parameters.
	 * 
	 * @param sub
	 *            Subject
	 * @param pre
	 *            Predicate
	 * @param obj
	 *            Object
	 * @param type
	 *            Type
	 * @return The query as {@link String}
	 */
	public static String createSelect(String table,
			Map<String, String> conditions) {

		StringBuilder query = new StringBuilder("SELECT * FROM " + table
				+ " WHERE ");
		for (String key : conditions.keySet()) {
			query.append(key + "='" + conditions.get(key) + "' AND ");
		}
		query.delete(query.length() - 5, query.length());
		query.append(';');
		return query.toString();
	}

	/**
	 * Creates an delete query with the given parameters.
	 * 
	 * @param sub
	 *            Subject
	 * @param pre
	 *            Predicate
	 * @param obj
	 *            Object
	 * @param type
	 *            Type
	 * @return The query as {@link String}
	 */
	public static String createDelete(String table, String sub, String pre,
			String obj) {

		StringBuilder query = new StringBuilder("DELETE FROM" + sp + table + sp
				+ "WHERE");
		if (null != sub)
			query.append(sp + Column.SUBJECT.value() + "=" + qm + sub + qm + sp
					+ and);
		if (null != pre)
			query.append(sp + Column.PREDICATE.value() + "=" + qm + pre + qm
					+ sp + and);
		if (null != obj)
			query.append(sp + Column.OBJECT.value() + "=" + qm + obj + qm + sp
					+ and);

		return query.substring(0, query.length() - 4);
	}

	public static String deleteOutgoingAssosiations(String table, String subject) {
		return "DELETE FROM " + table + " WHERE " + Column.SUBJECT.value()
				+ "='" + subject + "';";
	}

	public static String deleteIncommingAssosiations(String table, String object) {
		return "DELETE FROM " + table + " WHERE " + Column.OBJECT.value()
				+ "='" + object + "';";
	}
}