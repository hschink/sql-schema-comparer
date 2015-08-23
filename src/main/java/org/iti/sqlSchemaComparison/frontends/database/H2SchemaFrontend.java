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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.h2.jdbc.JdbcSQLException;
import org.iti.sqlSchemaComparison.edge.ForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.DefaultColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.NotNullColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.PrimaryKeyColumnConstraint;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public class H2SchemaFrontend implements ISqlSchemaFrontend {

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
	
	private static final String QUERY_TABLES = "SELECT table_name FROM information_schema.tables WHERE table_type='TABLE' ORDER BY table_name;";
	private static final String QUERY_TABLE_SCHEMA = "SELECT table_name, column_name, type_name, is_nullable, column_default FROM information_schema.columns WHERE table_name = ?;";
	private static final String QUERY_PRIMARY_KEY_COLUMN = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.CONSTRAINTS "
                                                           + "WHERE CONSTRAINT_TYPE = 'PRIMARY KEY' "
                                                           + "AND TABLE_NAME = ? "
                                                           + "AND COLUMN_LIST = ?;";
	private static final String QUERY_TABLE_SCHEMA_FOREIGN_KEYS = "SELECT SQL FROM INFORMATION_SCHEMA.CONSTRAINTS "
                                                                  + "WHERE CONSTRAINT_TYPE = 'REFERENTIAL' "
                                                                  + "AND TABLE_NAME = ? "
                                                                  + "AND COLUMN_LIST = ?;";
	
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
			connection = getH2Connection();
			queryTables = connection.prepareStatement(QUERY_TABLES);
			
			List<String> tables = getH2Tables();
			
			for (String table : tables) {
				createTableSchema(connection, schema, table);
			}
			
			createForeignKeyRelation(connection, schema);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JdbcSQLException e) {
			throw new IllegalArgumentException(e);
		}catch (SQLException e) {
			if (e.getMessage().contains("invalid database address")) {
				throw new IllegalArgumentException(e);
			}

			throw e;
		} finally {
			if (connection != null)
				connection.close();
		}
		
		return schema;
	}

	private Connection getH2Connection() throws SQLException, ClassNotFoundException {
		FileSystem fs = FileSystems.getDefault();
		Path path = fs.getPath(filePath);
		String url = "jdbc:h2:" + path;

        return DriverManager.getConnection(url);
	}

	private List<String> getH2Tables() throws SQLException {
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
		
		PreparedStatement stm = connection.prepareStatement(QUERY_TABLE_SCHEMA);

		stm.setString(1, tableName);
		
		ResultSet tableSchema = stm.executeQuery();
		
		while (tableSchema.next()) {
			String id = tableSchema.getString(ColumnSchema.NAME.getValue());
			String type = tableSchema.getString(ColumnSchema.TYPE.getValue()).toUpperCase();
			List<IColumnConstraint> constraints = new ArrayList<>();
			ISqlElement column = new SqlColumnVertex(id, type, table.getSqlElementId());
			
			if (tableSchema.getString(ColumnSchema.NOT_NULL.getValue()).equals("YES"))
				constraints.add(new NotNullColumnConstraint("", column));
			
			String defaultValue = tableSchema.getString(ColumnSchema.DEFAULT_VALUE.getValue());
			
			if (!tableSchema.wasNull())
				constraints.add(new DefaultColumnConstraint(defaultValue, column));
			
			if (isPrimaryKeyColumn(connection, tableName, id))
				constraints.add(new PrimaryKeyColumnConstraint("", column));
			
			((SqlColumnVertex) column).setConstraints(constraints);
			
			schema.addVertex(column);
			schema.addEdge(table, column, new TableHasColumnEdge(table, column));
		}
	}
	
	private boolean isPrimaryKeyColumn(Connection connection, String tableName, String columnName) throws SQLException {
		PreparedStatement stm = connection.prepareStatement(QUERY_PRIMARY_KEY_COLUMN);

		stm.setString(1, tableName);
		stm.setString(2, columnName);

		ResultSet primaryKeyCount = stm.executeQuery();

		return (primaryKeyCount.next()) ? primaryKeyCount.getInt(1) == 1 : false;
	}

	private static final String FOREIGN_KEY_REGEX = "REFERENCES \\w+\\.(?<table>\\w+)\\((?<column>\\w+)\\)";
	private static final Pattern FOREIGN_KEY_PATTERN = Pattern.compile(FOREIGN_KEY_REGEX);

	private void createForeignKeyRelation(Connection connection, DirectedGraph<IStructureElement, DefaultEdge> schema) throws SQLException {
		Set<ISqlElement> tables = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet());
		PreparedStatement stm = connection.prepareStatement(QUERY_TABLE_SCHEMA_FOREIGN_KEYS);
		
		for (ISqlElement table : tables) {
			Set<ISqlElement> columns = ((SqlTableVertex) table).getColumns(schema);

			for (ISqlElement column : columns) {
				stm.setString(1, table.getName());
				stm.setString(2, column.getName());

				try {
					ResultSet tableSchema = stm.executeQuery();

					if (tableSchema.next()) {
						String sql = tableSchema.getString(1);
						Matcher matcher = FOREIGN_KEY_PATTERN.matcher(sql);

						if (matcher.find()) {
							String foreignTable = matcher.group("table");
							String foreignColumn = foreignTable + "." + matcher.group("column");
							String referencingColumnName = table.getSqlElementId() + "." + column.getSqlElementId();

							ISqlElement foreignKeyTable = SqlElementFactory.getMatchingSqlElement(SqlElementType.Table, foreignTable, schema.vertexSet());
							ISqlElement foreignKeyColumn = SqlElementFactory.getMatchingSqlElement(SqlElementType.Column, foreignColumn, schema.vertexSet());
							ISqlElement referencingColumn = SqlElementFactory.getMatchingSqlElement(SqlElementType.Column, referencingColumnName, schema.vertexSet());
						
							schema.addEdge(referencingColumn, foreignKeyColumn, new ForeignKeyRelationEdge(referencingColumn, foreignKeyTable, foreignKeyColumn));
						}
					}
				} catch (Exception ex) {
					
				}
			}
		}
	}
	
	public H2SchemaFrontend(String filePath) {
		if (filePath == null || filePath == "")
			throw new InvalidPathException("", "Path to H2 database file must not be null or empty!");
		
		this.filePath = filePath;
	}
}
