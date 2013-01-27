package org.iti.sqlSchemaComparison;

import junit.framework.Assert;

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

	private static final String QUERY_WITH_MISSING_COLUMN = "SELECT firstname, surname, fee FROM customers;";
	private static final String QUERY_WITH_FOREIGN_TABLE_REFERENCE = "SELECT firstname, surname, account FROM customers;";
	
	private static Graph<ISqlElement, DefaultEdge> sqliteSchema;
	
	@BeforeClass
	public static void Init() {
		ISqlSchemaFrontend sqliteFrontend = new SqliteSchemaFrontend(SqliteSchemaFrontendTest.DATABASE_FILE_PATH);
		
		sqliteSchema = sqliteFrontend.createSqlSchema();
	}
	
	@Test
	public void SingleTableQueryIsValid() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(SqlStatementFrontendTest.SINGLE_TABLE_QUERY, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		Assert.assertTrue(result.isStatementValid());		
	}
	
	@Test
	public void QueryWithMissingColumn() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_MISSING_COLUMN, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		Assert.assertFalse(result.isStatementValid());	
		Assert.assertEquals(1, result.getMissingColumns().size());
		Assert.assertEquals("fee", result.getMissingColumns().get(0).getSqlElementId());
	}
	
	@Test
	public void QueryWithForeignTableReference() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(QUERY_WITH_FOREIGN_TABLE_REFERENCE, null);
		Graph<ISqlElement, DefaultEdge> expectedSchema = frontend.createSqlSchema();
		SqlStatementExpectationValidator validator = new SqlStatementExpectationValidator(sqliteSchema);
		
		SqlStatementExpectationValidationResult result = validator.computeGraphMatching(expectedSchema);
		
		ISqlElement key = result.getMissingButReachableColumns().keySet().iterator().next();
		
		Assert.assertFalse(result.isStatementValid());	
		Assert.assertEquals(1, result.getMissingButReachableColumns().size());
		Assert.assertEquals(2, result.getMissingButReachableColumns().get(key).size());
		Assert.assertEquals("account", key.getSqlElementId());
	}
	
	@Before
	public void setUp() { }
	
	@After
	public void tearDown() { }
}
