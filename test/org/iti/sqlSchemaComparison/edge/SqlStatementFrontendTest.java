package org.iti.sqlSchemaComparison.edge;

import junit.framework.Assert;

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqlStatementFrontendTest {

	public static final String SINGLE_TABLE_QUERY = "SELECT firstname, surname FROM customers;";
	private static final String JOIN_TABLE_QUERY = "SELECT firstname, surname, name FROM employees, departments;";
	private static final String JOIN_TABLE_WITH_ALIAS_QUERY = "SELECT e.firstname, e.surname, d.name FROM employees e, departments d;";
	private static final String JOIN_TABLE_WITH_TABLE_REFERENCE_QUERY = "SELECT employees.firstname, employees.surname, departments.name FROM employees, departments;";
	
	private static Graph<ISqlElement, DefaultEdge> sqliteSchema;
	
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
		
		Assert.assertEquals(1, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		Assert.assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void JoinTablQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		Assert.assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		Assert.assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void JoinTablWithAliasQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_WITH_ALIAS_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		Assert.assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		Assert.assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@Test
	public void JoinTablWithTableReferenceQuery() {
		ISqlSchemaFrontend frontend = new SqlStatementFrontend(JOIN_TABLE_WITH_TABLE_REFERENCE_QUERY, sqliteSchema);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		Assert.assertEquals(2, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		Assert.assertEquals(3, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}

	@After
	public void tearDown() { }
}
