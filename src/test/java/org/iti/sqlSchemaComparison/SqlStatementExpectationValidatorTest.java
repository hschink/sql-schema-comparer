package org.iti.sqlSchemaComparison;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
import org.jgrapht.Graph;
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
	public static final String QUERY_WITH_TWO_MISSING_TABLEs = "SELECT firstname, surname FROM missing_1, missing_2;";
	public static final String QUERY_WITH_FOREIGN_TABLE_REFERENCE = "SELECT firstname, surname, account FROM customers;";
	
	private static Graph<ISqlElement, DefaultEdge> sqliteSchema;
	
	@BeforeClass
	public static void Init() {
		ISqlSchemaFrontend sqliteFrontend = new SqliteSchemaFrontend(SqliteSchemaFrontendTest.DATABASE_FILE_PATH);
		
		sqliteSchema = sqliteFrontend.createSqlSchema();
	}
	
	@Before
	public void setUp() { }
	
	@Test
	public void SingleTableQueryIsValid() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(SqlStatementFrontendTest.SINGLE_TABLE_QUERY, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		assertTrue(result.isStatementValid());		
	}
	
	@Test
	public void QueryWithMissingColumn() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_COLUMN, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		assertFalse(result.isStatementValid());	
		assertEquals(1, result.getMissingColumns().size());
		assertEquals("fee", result.getMissingColumns().get(0).getSqlElementId());
	}
	
	@Test
	public void QueryWithTwoMissingColumns() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TWO_MISSING_COLUMNS, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
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
	public void QueryWithMissingTable() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_TABLE, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		assertFalse(result.isStatementValid());	
		assertEquals(1, result.getMissingTables().size());
		assertEquals("missing", result.getMissingTables().get(0).getSqlElementId());
	}
	
	@Test
	public void QueryWithTwoMissingTables() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_TWO_MISSING_TABLEs, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
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
	public void QueryWithForeignTableReference() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_FOREIGN_TABLE_REFERENCE, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		ISqlElement key = result.getMissingButReachableColumns().keySet().iterator().next();
		
		assertFalse(result.isStatementValid());	
		assertEquals(1, result.getMissingButReachableColumns().size());
		assertEquals(2, result.getMissingButReachableColumns().get(key).size());
		assertEquals("account", key.getSqlElementId());
	}
	
	@Test
	public void SourceElementIsNotEmptyForMissingColumn() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_COLUMN, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		for (ISqlElement element : result.getMissingColumns()) {
			assertTrue(element.getSourceElement() != null);
			assertTrue(element.getSourceElement() instanceof ZSelectItem);
		}
	}
	
	@Test
	public void SourceElementIsNotEmptyForMissingTable() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_TABLE, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		for (ISqlElement element : result.getMissingTables()) {
			assertTrue(element.getSourceElement() != null);
			assertTrue(element.getSourceElement() instanceof ZFromItem);
		}
	}
	
	@After
	public void tearDown() { }
}
