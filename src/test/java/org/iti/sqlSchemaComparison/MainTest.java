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
