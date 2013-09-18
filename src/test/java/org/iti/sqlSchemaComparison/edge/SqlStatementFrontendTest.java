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

package org.iti.sqlSchemaComparison.edge;

import static org.junit.Assert.*;

import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.frontends.SqlStatementFrontend;
import org.iti.sqlSchemaComparison.frontends.database.SqliteSchemaFrontend;
import org.iti.sqlSchemaComparison.frontends.database.SqliteSchemaFrontendTest;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqlStatementFrontendTest {

	public static final String SINGLE_TABLE_QUERY = "SELECT firstname, surname FROM customers;";
	private static final String JOIN_TABLE_QUERY = "SELECT firstname, surname, name FROM employees, departments;";
	private static final String JOIN_TABLE_WITH_ALIAS_QUERY = "SELECT e.firstname, e.surname, d.name FROM employees e, departments d;";
	private static final String JOIN_TABLE_WITH_TABLE_REFERENCE_QUERY = "SELECT employees.firstname, employees.surname, departments.name FROM employees, departments;";
	private static final String TABLE_DOES_NOT_EXIST_QUERY = "SELECT name from wrong_table;";
	private static final String COLUMN_DOES_NOT_EXIST_QUERY = "SELECT wrong_column from customers;";
	private static final String MULTIPLE_MATCHING_COLUMNS_QUERY = "SELECT account from managers, salespersons;";
	private static final String QUERY_WITH_TABLE_PREFIXED_COLUMNS_AND_WRONG_TABLE = "SELECT customers.firstname, department.name FROM customers, departments;";
	private static final String QUERY_WITH_TABLE_PREFIXED_COLUMNS_AND_AMBIGUOUS_TABLES = "SELECT firstname, name FROM customers, departments;";
	
	private static Graph<ISqlElement, DefaultEdge> sqliteSchema;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void init() {
		ISqlSchemaFrontend sqliteFrontend = new SqliteSchemaFrontend(SqliteSchemaFrontendTest.DATABASE_FILE_PATH);
		
		sqliteSchema = sqliteFrontend.createSqlSchema();
	}
	
	@Before
	public void setUp() { }
	
	@Test
	public void singleTableQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(SINGLE_TABLE_QUERY, null);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertEquals(1, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void joinTablQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void joinTablWithAliasQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_WITH_ALIAS_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void joinTablWithTableReferenceQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_WITH_TABLE_REFERENCE_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void queryOnNonExistingTable() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(TABLE_DOES_NOT_EXIST_QUERY, sqliteSchema);

		frontend.createSqlSchema();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void queryOnNonExistingColumn() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(COLUMN_DOES_NOT_EXIST_QUERY, sqliteSchema);

		frontend.createSqlSchema();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void queryWithMultipleMatchingColumns() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(MULTIPLE_MATCHING_COLUMNS_QUERY, sqliteSchema);

		frontend.createSqlSchema();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void queryWithTablePrefixedColumnsAndWrongTable() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TABLE_PREFIXED_COLUMNS_AND_WRONG_TABLE, null);

		frontend.createSqlSchema();
	}

	@Test(expected=IllegalArgumentException.class)
	public void queryWithTablePrefixedColumnsAndAmbiguousTables() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TABLE_PREFIXED_COLUMNS_AND_AMBIGUOUS_TABLES, null);

		frontend.createSqlSchema();
	}

	@After
	public void tearDown() { }
}
