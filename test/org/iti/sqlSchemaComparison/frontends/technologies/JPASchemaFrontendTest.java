package org.iti.sqlSchemaComparison.frontends.technologies;

import junit.framework.Assert;

import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JPASchemaFrontendTest {

	private static final String JPA_FILE_PATH = "jpa\\Employee.java";
	
	@Before
	public void setUp() { }
	
	@Test
	public void DatabaseConnectionEstablishedCorrectly() {
		ISqlSchemaFrontend frontend = new JPASchemaFrontend(JPA_FILE_PATH);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		Assert.assertNotNull(schema);
		Assert.assertEquals(1, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		Assert.assertEquals(6, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
	}
	
	@After
	public void tearDown() { }

}
