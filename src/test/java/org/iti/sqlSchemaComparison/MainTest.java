package org.iti.sqlSchemaComparison;

import org.iti.sqlSchemaComparison.edge.SqlStatementFrontendTest;
import org.iti.sqlSchemaComparison.frontends.database.SqliteSchemaFrontendTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MainTest {

	@Before
	public void setUp() { }
	
	@Test
	public void DatabaseSchemaComparison() {
		String[] args = new String[] { 
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DROPPED_COLUMN_DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DROPPED_TABLE_DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.MOVE_COLUMN_DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.RENAME_COLUMN_DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.RENAME_TABLE_DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.REPLACE_COLUMN_DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH,
			SqliteSchemaFrontendTest.REPLACE_LOB_WITH_TABLE_DATABASE_FILE_PATH
		};
		
		Main.main(args);
	}
	
	@Test
	public void StatementValidation() {
		String[] args = new String[] {
			"-statement",
			SqlStatementFrontendTest.SINGLE_TABLE_QUERY,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH
		};
		
		Main.main(args);
	}
	
	@Test
	public void StatementValidationMissingColumn() {
		String[] args = new String[] {
			"-statement",
			SqlStatementExpectationValidatorTest.QUERY_WITH_MISSING_COLUMN,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH
		};
		
		Main.main(args);
	}
	
	@Test
	public void StatementValidationMissingButReachableColumn() {
		String[] args = new String[] {
			"-statement",
			SqlStatementExpectationValidatorTest.QUERY_WITH_FOREIGN_TABLE_REFERENCE,
			SqliteSchemaFrontendTest.DATABASE_FILE_PATH
		};
		
		Main.main(args);
	}
	
	@After
	public void tearDown() { }
}
