/*
 *  Copyright 1999 Hagen Schink <hagen.schink@gmail.com>
 *
 *  This file is part of sql-schema-comparer.
 *
 *  sql-schema-comparer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  sql-schema-comparer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with sql-schema-comparer.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.iti.sqlSchemaComparison.frontends;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.gibello.zql.ParseException;
import org.gibello.zql.ZFromItem;
import org.gibello.zql.ZQuery;
import org.gibello.zql.ZSelectItem;
import org.gibello.zql.ZStatement;
import org.gibello.zql.ZqlParser;
import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public class SqlStatementFrontend implements ISqlSchemaFrontend {

	private String statement;

	public String getStatement() {
		return statement;
	}

	private DirectedGraph<IStructureElement, DefaultEdge> databaseSchema;

	public DirectedGraph<IStructureElement, DefaultEdge> getDatabaseSchema() {
		return databaseSchema;
	}

	@Override
	public DirectedGraph<IStructureElement, DefaultEdge> createSqlSchema() {
		DirectedGraph<IStructureElement, DefaultEdge> result = null;
		ZStatement statement = parseStatement();

		if (statement != null)
			result = createGraph(statement);

		return result;
	}

	private ZStatement parseStatement() {
		try {
			InputStream is = new ByteArrayInputStream(statement.getBytes("UTF-8"));
			ZqlParser parser = new ZqlParser(is);

			return parser.readStatement();
		} catch (UnsupportedEncodingException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private DirectedGraph<IStructureElement, DefaultEdge> createGraph(ZStatement statement) {
		DirectedGraph<IStructureElement, DefaultEdge> result = null;

		if (statement instanceof ZQuery)
			result = createGraphFromQuery((ZQuery)statement);

		return result;
	}

	private DirectedGraph<IStructureElement, DefaultEdge> createGraphFromQuery(ZQuery query) {
		DirectedGraph<IStructureElement, DefaultEdge> schema = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);

		createTables(query, schema);

		createTableColumns(query, schema);

		return schema;
	}

	private void createTables(ZQuery query,
			DirectedGraph<IStructureElement, DefaultEdge> schema) {

		for (Object item : query.getFrom()) {
			ZFromItem fromItem = (ZFromItem) item;

			createTable(schema, query, fromItem);
		}
	}

	private void createTable(DirectedGraph<IStructureElement, DefaultEdge> schema,
			ZQuery query, ZFromItem fromItem) {
		if (databaseSchema != null) {
			ISqlElement table = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, fromItem.getTable(), databaseSchema.vertexSet());

			if (table == null)
				throw new IllegalArgumentException(String.format("Table %s does not exist in schema!", fromItem.getTable()));
		}

		ISqlElement table = SqlElementFactory.createSqlElement(SqlElementType.Table, fromItem.getTable());

		table.setSourceElement(fromItem);

		schema.addVertex(table);
	}

	private void createTableColumns(ZQuery query,
			DirectedGraph<IStructureElement, DefaultEdge> schema) {

		for (Object item : query.getSelect()) {
			ZSelectItem selectItem = (ZSelectItem) item;

			ZFromItem tableItem = getColumnTable(query, selectItem);

			ISqlElement table = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, tableItem.getTable(), schema.vertexSet());
			ISqlElement column = new SqlColumnVertex(selectItem.getColumn(), null, table.getName());

			column.setSourceElement(selectItem);

			schema.addVertex(column);
			schema.addEdge(table, column, new TableHasColumnEdge(table, column));
		}
	}

	private ZFromItem getColumnTable(ZQuery query, ZSelectItem selectItem) {
		if (selectItem.getTable() != null) {
			return getColumnTableFromQuery(query, selectItem);
		} else if (databaseSchema != null) {
			return getColumnTableFromDatabaseSchema(query, selectItem);
		} else if (query.getFrom().size() == 1) {
			return (ZFromItem) query.getFrom().get(0);
		}

		throw new IllegalArgumentException("No matching table for column " + selectItem.getColumn() + " exists!");
	}

	private ZFromItem getColumnTableFromQuery(ZQuery query, ZSelectItem selectItem) {
		String tableName = selectItem.getTable();

		for (Object item : query.getFrom()) {
			ZFromItem tableItem = (ZFromItem)item;

			if (tableItem.getTable().equals(tableName)
					|| (tableItem.getAlias() != null && tableItem.getAlias().equals(tableName))) {
				return tableItem;
			}
		}

		throw new IllegalArgumentException("Table " + selectItem.getTable()
				   + " for column " + selectItem.getColumn()
				   + " does not exist!");
	}

	private ZFromItem getColumnTableFromDatabaseSchema(ZQuery query,
			ZSelectItem selectItem) {
		Set<ZFromItem> tables = getTablesFromQuery(query);
		Set<String> matchingTables = getTablesContainingColumn(tables, selectItem.getColumn());

		if (matchingTables.size() > 1)
			throw new IllegalArgumentException("Column " + selectItem.getColumn() + " is part of more than one table!");

		if (matchingTables.size() == 0)
			throw new IllegalArgumentException("No matching table for column " + selectItem.getColumn() + " exists!");

		return getTableFromQuery(query, (String) matchingTables.toArray()[0]);
	}

	private Set<ZFromItem> getTablesFromQuery(ZQuery query) {
		Set<ZFromItem> tables = new HashSet<>();

		for (Object item : query.getFrom())
			tables.add((ZFromItem) item);

		return tables;
	}

	private ZFromItem getTableFromQuery(ZQuery query, String tableName) {
		ZFromItem result = null;

		for (Object item : query.getFrom()) {
			ZFromItem tableItem = (ZFromItem) item;

			if (tableItem.getTable().equals(tableName)) {
				result = tableItem;
				break;
			}
		}

		return result;
	}

	private Set<String> getTablesContainingColumn(Set<ZFromItem> tables, String columnName) {
		Set<String> matchingTables = new HashSet<>();

		for (ZFromItem tableItem : tables) {
			String tableName = tableItem.getTable();
			ISqlElement table = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, tableName, databaseSchema.vertexSet());


			for (DefaultEdge e : databaseSchema.edgeSet()) {
				if (e instanceof TableHasColumnEdge) {
					TableHasColumnEdge edge = (TableHasColumnEdge) e;

					if (edge.getTable().equals(table) && edge.getColumn().getName().equals(columnName))
						matchingTables.add(table.getName());
				}
			}
		}

		return matchingTables;
	}

	public SqlStatementFrontend(String statement, DirectedGraph<IStructureElement, DefaultEdge> databaseSchema) {
		if (statement == null || statement == "")
			throw new NullPointerException("SQL statement must not be null or empty!");

		this.statement = statement;
		this.databaseSchema = databaseSchema;
	}

}
