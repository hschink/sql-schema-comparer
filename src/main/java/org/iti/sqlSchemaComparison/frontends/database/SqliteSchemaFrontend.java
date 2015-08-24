/*
 *  Copyright 1999 Hagen Schink <hagen.schink@gmail.com>
 *
 *  This file is part of sql-schema-comparer.
 *
 *  sql-schema-comparer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  sql-schema-comparer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with sql-schema-comparer.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.iti.sqlSchemaComparison.frontends.database;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.iti.sqlSchemaComparison.edge.ColumnHasConstraint;
import org.iti.sqlSchemaComparison.edge.ForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.ColumnConstraintVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint.ConstraintType;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public class SqliteSchemaFrontend implements ISqlSchemaFrontend {

	private enum ColumnSchema {
		NAME(2),
		TYPE(3),
		NOT_NULL(4),
		DEFAULT_VALUE(5),
		PRIMARY_KEY(6);

		private int value;

		private ColumnSchema(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private static final String QUERY_TABLES = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;";
	private static final String QUERY_TABLE_SCHEMA = "PRAGMA table_info(?)";
	private static final String QUERY_TABLE_SCHEMA_FOREIGN_KEYS = "PRAGMA foreign_key_list(?)";

	private PreparedStatement queryTables = null;

	private String filePath;

	@Override
	public DirectedGraph<IStructureElement, DefaultEdge> createSqlSchema() {
		DirectedGraph<IStructureElement, DefaultEdge> schema = null;

		try {
			schema = tryCreateSqlSchema();
		} catch (IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return schema;
	}

	private DirectedGraph<IStructureElement, DefaultEdge> tryCreateSqlSchema() throws SQLException {
		DirectedGraph<IStructureElement, DefaultEdge> schema = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
		Connection connection = null;

		try {
			connection = getSqliteConnection();
			queryTables = connection.prepareStatement(QUERY_TABLES);

			List<String> tables = getSqliteTables();

			for (String table : tables) {
				createTableSchema(connection, schema, table);
			}

			createForeignKeyRelation(connection, schema);

		} finally {
			if (connection != null)
				connection.close();
		}

		return schema;
	}

	private Connection getSqliteConnection() throws SQLException {
		FileSystem fs = FileSystems.getDefault();
		Path path = fs.getPath(filePath);

		if (!path.toFile().exists())
			throw new IllegalArgumentException("SQLite database file does not exist!");

		return DriverManager.getConnection("jdbc:sqlite:" + path);
	}

	private List<String> getSqliteTables() throws SQLException {
		List<String> tables = new ArrayList<>();

		ResultSet result = queryTables.executeQuery();

		while (result.next())
			tables.add(result.getString(1));

		return tables;
	}

	private void createTableSchema(Connection connection, DirectedGraph<IStructureElement, DefaultEdge> schema,
			String tableName) throws SQLException {
		ISqlElement table = SqlElementFactory.createSqlElement(SqlElementType.Table, tableName);
		schema.addVertex(table);

		Statement stm = connection.createStatement();

		ResultSet tableSchema = stm.executeQuery(QUERY_TABLE_SCHEMA.replaceAll("\\?", tableName));

		while (tableSchema.next()) {
			createColumn(schema, table, tableSchema);
		}
	}

	private void createColumn(DirectedGraph<IStructureElement, DefaultEdge> schema, ISqlElement table,
			ResultSet tableSchema) throws SQLException {
		String id = tableSchema.getString(ColumnSchema.NAME.getValue());
		String type = tableSchema.getString(ColumnSchema.TYPE.getValue()).toUpperCase();
		ISqlElement column = new SqlColumnVertex(id, type, table.getName());

		schema.addVertex(column);
		schema.addEdge(table, column, new TableHasColumnEdge(table, column));

		createColumnConstraints(schema, tableSchema, id, column);
	}

	private void createColumnConstraints(DirectedGraph<IStructureElement, DefaultEdge> schema, ResultSet tableSchema,
			String columnName, ISqlElement column) throws SQLException {
		if (tableSchema.getInt(ColumnSchema.NOT_NULL.getValue()) > 0)
			addColumnConstraint(new ColumnConstraintVertex(columnName, ConstraintType.NOT_NULL), schema, column);

		String defaultValue = tableSchema.getString(ColumnSchema.DEFAULT_VALUE.getValue());

		if (!tableSchema.wasNull())
			addColumnConstraint(new ColumnConstraintVertex(columnName, ConstraintType.DEFAULT, defaultValue), schema, column);

		if (tableSchema.getInt(ColumnSchema.PRIMARY_KEY.getValue()) > 0)
			addColumnConstraint(new ColumnConstraintVertex(columnName, ConstraintType.PRIMARY_KEY), schema, column);
	}

	private void addColumnConstraint(ColumnConstraintVertex columnConstraint,
			DirectedGraph<IStructureElement, DefaultEdge> schema, ISqlElement column) {
		schema.addVertex(columnConstraint);
		schema.addEdge(column, columnConstraint, new ColumnHasConstraint());
	}

	private enum ForeignKeySchema {
		TABLE(3),
		FROM(4),
		TO(5);

		private int value;

		private ForeignKeySchema(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private void createForeignKeyRelation(Connection connection, DirectedGraph<IStructureElement, DefaultEdge> schema) throws SQLException {
		Set<ISqlElement> tables = SqlElementFactory.getSqlElementsOfType(SqlTableVertex.class, schema.vertexSet());
		Statement stm = connection.createStatement();

		for (ISqlElement table : tables) {
			try {
				ResultSet tableSchema = stm.executeQuery(QUERY_TABLE_SCHEMA_FOREIGN_KEYS.replaceAll("\\?", table.getName()));

				while (tableSchema != null && tableSchema.next()) {
					String foreignTable = tableSchema.getString(ForeignKeySchema.TABLE.getValue());
					String foreignColumn = foreignTable + "." + tableSchema.getString(ForeignKeySchema.TO.getValue());
					String referencingColumnName = table.getName() + "." + tableSchema.getString(ForeignKeySchema.FROM.getValue());

					ISqlElement foreignKeyTable = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, foreignTable, schema.vertexSet());
					ISqlElement foreignKeyColumn = SqlElementFactory.getMatchingSqlElement(SqlColumnVertex.class, foreignColumn, schema.vertexSet());
					ISqlElement referencingColumn = SqlElementFactory.getMatchingSqlElement(SqlColumnVertex.class, referencingColumnName, schema.vertexSet());

					schema.addEdge(referencingColumn, foreignKeyColumn, new ForeignKeyRelationEdge(referencingColumn, foreignKeyTable, foreignKeyColumn));
				}
			} catch (Exception ex) {

			}
		}
	}

	public SqliteSchemaFrontend(String filePath) {
		if (filePath == null || filePath == "")
			throw new InvalidPathException("", "Path to SQLite database file must not be null or empty!");

		this.filePath = filePath;
	}
}
