package org.iti.sqlSchemaComparison.frontends;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.iti.sqlSchemaComparison.SqlSchemaComparer;
import org.iti.sqlSchemaComparison.SqlSchemaComparisonResult;
import org.iti.sqlSchemaComparison.edge.ForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.PrimaryKeyColumnConstraint;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqliteSchemaFrontendTest {

	private static final String DATABASE_FILE_PATH = "test\\databases\\hrm.sqlite";
	private static final String DROPPED_COLUMN_DATABASE_FILE_PATH = "test\\databases\\refactored\\hrm_DropColumn.sqlite";
	private static final String DROPPED_TABLE_DATABASE_FILE_PATH = "test\\databases\\refactored\\hrm_DropTable.sqlite";
	
	private static final String DROPPED_COLUMN_NAME = "boss";
	private static final String DROPPED_TABLE_NAME = "external_staff";
	
	@Before
	public void setUp() { }
	
	@Test
	public void DatabaseConnectionEstablishedCorrectly() {
		ISqlSchemaFrontend frontend = new SqliteSchemaFrontend(DATABASE_FILE_PATH);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		Assert.assertNotNull(schema);
		Assert.assertEquals(8, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		Assert.assertEquals(31, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
		Assert.assertEquals(7, getColumnWithConstraint(SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()), PrimaryKeyColumnConstraint.class).size());
		
	}
	
	@Test
	public void ForeignKeysEstablishedCorrectly() {
		ISqlSchemaFrontend frontend = new SqliteSchemaFrontend(DATABASE_FILE_PATH);
		Graph<ISqlElement, DefaultEdge> schema = frontend.createSqlSchema();
		int foreignKeyEdges = 0;
		
		for (DefaultEdge edge : schema.edgeSet())
			if (edge instanceof ForeignKeyRelationEdge)
				foreignKeyEdges++;
		
		
		Assert.assertEquals(7, foreignKeyEdges);
		
	}
	
	private List<ISqlElement> getColumnWithConstraint(Set<ISqlElement> sqlElementsOfType, Class<?> constraintType) {
		List<ISqlElement> columns = new ArrayList<>();
		
		for (ISqlElement e : sqlElementsOfType) {
			if (e instanceof SqlColumnVertex)
				for (IColumnConstraint c : ((SqlColumnVertex) e).getConstraints())
					if (constraintType.isAssignableFrom(c.getClass())) {
						columns.add(e);
						break;
					}
		}
		
		return columns;
	}
	
	@Test
	public void DroppedColumnDetectedCorrectly() {
		ISqlSchemaFrontend frontend1 = new SqliteSchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new SqliteSchemaFrontend(DROPPED_COLUMN_DATABASE_FILE_PATH);
		Graph<ISqlElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		Graph<ISqlElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;
		
		Assert.assertEquals(31, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet()).size());
		Assert.assertEquals(30, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet()).size());
		Assert.assertNotNull(result.getRemovedColumn());
		Assert.assertEquals(DROPPED_COLUMN_NAME, result.getRemovedColumn().getSqlElementId());
	}
	
	@Test
	public void DroppedTableDetectedCorrectly() {
		ISqlSchemaFrontend frontend1 = new SqliteSchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new SqliteSchemaFrontend(DROPPED_TABLE_DATABASE_FILE_PATH);
		Graph<ISqlElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		Graph<ISqlElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;
		
		Assert.assertEquals(8, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema1.vertexSet()).size());
		Assert.assertEquals(7, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema2.vertexSet()).size());
		Assert.assertNotNull(result.getRemovedTable());
		Assert.assertEquals(DROPPED_TABLE_NAME, result.getRemovedTable().getSqlElementId());
	}

	@After
	public void tearDown() { }
}
