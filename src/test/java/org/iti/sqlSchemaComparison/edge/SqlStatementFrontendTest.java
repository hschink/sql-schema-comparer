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

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;

import org.gibello.zql.ParseException;
import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.frontends.SqlStatementFrontend;
import org.iti.sqlSchemaComparison.frontends.database.SqliteSchemaFrontend;
import org.iti.sqlSchemaComparison.frontends.database.SqliteSchemaFrontendTest;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
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

	private static DirectedGraph<IStructureElement, DefaultEdge> sqliteSchema;

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
	public void singleTableQuery() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(SINGLE_TABLE_QUERY, null);
		DirectedGraph<IStructureElement, DefaultEdge> schema = frontend.createSqlSchema();

		assertEquals(1, SqlElementFactory.getSqlElementsOfType(SqlTableVertex.class, schema.vertexSet()).size());
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlColumnVertex.class, schema.vertexSet()).size());
	}

	@Test
	public void joinTablQuery() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_QUERY, sqliteSchema);
		DirectedGraph<IStructureElement, DefaultEdge> schema = frontend.createSqlSchema();

		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlTableVertex.class, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlColumnVertex.class, schema.vertexSet()).size());
	}

	@Test
	public void joinTablWithAliasQuery() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_WITH_ALIAS_QUERY, sqliteSchema);
		DirectedGraph<IStructureElement, DefaultEdge> schema = frontend.createSqlSchema();

		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlTableVertex.class, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlColumnVertex.class, schema.vertexSet()).size());
	}

	@Test
	public void joinTablWithTableReferenceQuery() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_WITH_TABLE_REFERENCE_QUERY, sqliteSchema);
		DirectedGraph<IStructureElement, DefaultEdge> schema = frontend.createSqlSchema();

		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlTableVertex.class, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlColumnVertex.class, schema.vertexSet()).size());
	}

	@Test(expected=IllegalArgumentException.class)
	public void queryOnNonExistingTable() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(TABLE_DOES_NOT_EXIST_QUERY, sqliteSchema);

		frontend.createSqlSchema();
	}

	@Test(expected=IllegalArgumentException.class)
	public void queryOnNonExistingColumn() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(COLUMN_DOES_NOT_EXIST_QUERY, sqliteSchema);

		frontend.createSqlSchema();
	}

	@Test(expected=IllegalArgumentException.class)
	public void queryWithMultipleMatchingColumns() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(MULTIPLE_MATCHING_COLUMNS_QUERY, sqliteSchema);

		frontend.createSqlSchema();
	}

	@Test(expected=IllegalArgumentException.class)
	public void queryWithTablePrefixedColumnsAndWrongTable() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TABLE_PREFIXED_COLUMNS_AND_WRONG_TABLE, null);

		frontend.createSqlSchema();
	}

	@Test(expected=IllegalArgumentException.class)
	public void queryWithTablePrefixedColumnsAndAmbiguousTables() throws UnsupportedEncodingException, ParseException {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TABLE_PREFIXED_COLUMNS_AND_AMBIGUOUS_TABLES, null);

		frontend.createSqlSchema();
	}

	@After
	public void tearDown() { }
}
