package org.iti.sqlSchemaComparison;

import static org.junit.Assert.*;

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
	
	@After
	public void tearDown() { }
}
