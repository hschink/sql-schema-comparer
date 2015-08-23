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

package org.iti.sqlSchemaComparison;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.gibello.zql.ZFromItem;
import org.gibello.zql.ZSelectItem;
import org.iti.sqlSchemaComparison.edge.SqlStatementFrontendTest;
import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.frontends.SqlStatementFrontend;
import org.iti.sqlSchemaComparison.frontends.database.SqliteSchemaFrontend;
import org.iti.sqlSchemaComparison.frontends.database.SqliteSchemaFrontendTest;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqlStatementExpectationValidatorTest {

	public static final String QUERY_WITH_MISSING_COLUMN = "SELECT firstname, surname, fee FROM customers;";
	public static final String QUERY_WITH_TWO_MISSING_COLUMNS = "SELECT firstname, surname, missing_1, missing_2 FROM customers;";
	public static final String QUERY_WITH_MISSING_TABLE = "SELECT firstname, surname FROM missing;";
	public static final String QUERY_WITH_TWO_MISSING_TABLES = "SELECT missing_1.firstname, missing_2.surname FROM missing_1, missing_2;";
	public static final String QUERY_WITH_FOREIGN_TABLE_REFERENCE = "SELECT firstname, surname, account FROM customers;";
	public static final String QUERY_WITH_TABLE_PREFIXED_COLUMNS_AND_MISSING_COLUMN = "SELECT customers.firstname, customers.name FROM customers, departments;";

	private static DirectedGraph<IStructureElement, DefaultEdge> sqliteSchema;

	@BeforeClass
	public static void init() {
		ISqlSchemaFrontend sqliteFrontend = new SqliteSchemaFrontend(SqliteSchemaFrontendTest.DATABASE_FILE_PATH);

		sqliteSchema = sqliteFrontend.createSqlSchema();
	}

	@Before
	public void setUp() { }

	@Test
	public void singleTableQueryIsValid() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(SqlStatementFrontendTest.SINGLE_TABLE_QUERY, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		assertTrue(result.isStatementValid());
	}

	@Test
	public void queryWithMissingColumn() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_COLUMN, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		assertFalse(result.isStatementValid());
		assertEquals(1, result.getMissingColumns().size());
		assertEquals("fee", result.getMissingColumns().get(0).getSqlElementId());
	}

	@Test
	public void queryWithTwoMissingColumns() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TWO_MISSING_COLUMNS, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		assertFalse(result.isStatementValid());
		assertEquals(2, result.getMissingColumns().size());

		List<String> columnNames = new ArrayList<>();

		for (ISqlElement element : result.getMissingColumns()) {
			columnNames.add(element.getSqlElementId());
		}

		assertThat(columnNames, hasItems("missing_1", "missing_2"));
	}

	@Test
	public void queryWithMissingTable() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_TABLE, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		assertFalse(result.isStatementValid());
		assertEquals(1, result.getMissingTables().size());
		assertEquals("missing", result.getMissingTables().get(0).getSqlElementId());
	}

	@Test
	public void queryWithTwoMissingTables() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TWO_MISSING_TABLES, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		assertFalse(result.isStatementValid());
		assertEquals(2, result.getMissingTables().size());

		List<String> tableNames = new ArrayList<>();

		for (ISqlElement element : result.getMissingTables()) {
			tableNames.add(element.getSqlElementId());
		}

		assertThat(tableNames, hasItems("missing_1", "missing_2"));
	}

	@Test
	public void queryWithForeignTableReference() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_FOREIGN_TABLE_REFERENCE, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		ISqlElement key = result.getMissingButReachableColumns().keySet().iterator().next();

		assertFalse(result.isStatementValid());
		assertEquals(1, result.getMissingButReachableColumns().size());
		assertEquals(2, result.getMissingButReachableColumns().get(key).size());
		assertEquals("account", key.getSqlElementId());
	}

	@Test
	public void sourceElementIsNotEmptyForMissingColumn() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_COLUMN, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		for (ISqlElement element : result.getMissingColumns()) {
			assertNotNull(element.getSourceElement());
			assertSame(element.getSourceElement().getClass(), ZSelectItem.class);
		}
	}

	@Test
	public void sourceElementIsNotEmptyForMissingTable() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_TABLE, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		for (ISqlElement element : result.getMissingTables()) {
			assertNotNull(element.getSourceElement());
			assertSame(element.getSourceElement().getClass(), ZFromItem.class);
		}
	}

	@Test
	public void queryWithTablePrefixedColumnsAndMissingColumn() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TABLE_PREFIXED_COLUMNS_AND_MISSING_COLUMN, null);
		DirectedGraph<IStructureElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);

		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);

		assertFalse(result.isStatementValid());
		assertEquals(1, result.getMissingButReachableColumns().size());
		assertEquals("name", result.getMissingButReachableColumns().keySet().iterator().next().getSqlElementId());
	}

	@After
	public void tearDown() { }
}
