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
	
	private static Graph<ISqlElement, DefaultEdge> sqliteSchema;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void Init() {
		ISqlSchemaFrontend sqliteFrontend = new SqliteSchemaFrontend(SqliteSchemaFrontendTest.DATABASE_FILE_PATH);
		
		sqliteSchema = sqliteFrontend.createSqlSchema();
	}
	
	@Before
	public void setUp() { }
	
	@Test
	public void SingleTableQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(SINGLE_TABLE_QUERY, null);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertEquals(1, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void JoinTablQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void JoinTablWithAliasQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_WITH_ALIAS_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void JoinTablWithTableReferenceQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_WITH_TABLE_REFERENCE_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void QueryOnNonExistingTable() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(TABLE_DOES_NOT_EXIST_QUERY, sqliteSchema);
		
		exception.expect(IllegalArgumentException.class);
		frontend.createSqlSchema();
	}
	
	@Test
	public void QueryOnNonExistingColumn() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(COLUMN_DOES_NOT_EXIST_QUERY, sqliteSchema);
		
		exception.expect(IllegalArgumentException.class);
		frontend.createSqlSchema();
	}
	
	@Test
	public void QueryWithMultipleMatchingColumns() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(MULTIPLE_MATCHING_COLUMNS_QUERY, sqliteSchema);
		
		exception.expect(IllegalArgumentException.class);
		frontend.createSqlSchema();
	}

	@After
	public void tearDown() { }
}
