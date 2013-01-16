package org.iti.sqlSchemaComparison;

import junit.framework.Assert;

import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.jgrapht.Graph;
import org.jgrapht.experimental.isomorphism.IsomorphismRelation;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqlSchemaComparerTest {

	private static Graph<ISqlElement, DefaultEdge> schema1 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema2 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema3 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema4 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema5 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema6 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema7 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema8 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema9 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	
	private static ISqlElement t1 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t1");
	private static ISqlElement t2 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t2");
	
	private static ISqlElement c1 = SqlElementFactory.createSqlElement(SqlElementType.Column, "c1");
	private static ISqlElement c12 = SqlElementFactory.createSqlElement(SqlElementType.Column, "c12");
	private static ISqlElement c2 = SqlElementFactory.createSqlElement(SqlElementType.Column, "c2");
	private static ISqlElement c22 = SqlElementFactory.createSqlElement(SqlElementType.Column, "c22");
	
	@BeforeClass
	public static void Init() throws Exception {		
		schema1.addVertex(t1);
		schema2.addVertex(t1);
		schema2.addVertex(t2);
		schema3.addVertex(t1);
		schema3.addVertex(t2);
		schema4.addVertex(t1);
		schema4.addVertex(t2);
		schema5.addVertex(t1);
		schema5.addVertex(t2);
		schema6.addVertex(t1);
		schema7.addVertex(t1);
		schema9.addVertex(t2);
		
		schema1.addVertex(c1);
		schema2.addVertex(c1);
		schema2.addVertex(c12);
		schema2.addVertex(c2);
		schema2.addVertex(c22);
		schema3.addVertex(c1);
		schema3.addVertex(c12);
		schema3.addVertex(c2);
		schema3.addVertex(c22);
		schema4.addVertex(c1);
		schema4.addVertex(c2);
		schema5.addVertex(c1);
		schema5.addVertex(c2);
		schema6.addVertex(c2);
		schema9.addVertex(c1);
		
		schema1.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		
		schema2.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema2.addEdge(t1, c12, new TableHasColumnEdge(t1, c12));
		schema2.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		schema2.addEdge(t2, c22, new TableHasColumnEdge(t2, c22));
		
		schema3.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema3.addEdge(t1, c12, new TableHasColumnEdge(t1, c12));
		schema3.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		schema3.addEdge(t2, c22, new TableHasColumnEdge(t2, c22));
		
		schema4.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema4.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		
		schema5.addEdge(t2, c1, new TableHasColumnEdge(t2, c1));
		schema5.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		
		schema6.addEdge(t1, c2, new TableHasColumnEdge(t1, c2));
		
		schema9.addEdge(t2, c1, new TableHasColumnEdge(t2, c1));
	}
	
	@Before
	public void setUp() { }

	@Test
	public void IsomorphicGraphsAreDetectedCorrectly()  {
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema2, schema3);
		
		Assert.assertTrue(comparer.isIsomorphic());
	}
	
	@Test
	public void IsomorphismMapsVerticesCorrectly()  {
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema2, schema3);

		Assert.assertTrue(comparer.isIsomorphic());
		
		for (IsomorphismRelation<ISqlElement, Graph<ISqlElement, DefaultEdge>> isomorphism : comparer.getIsomorphisms()) {
			Assert.assertEquals(t1, isomorphism.getVertexCorrespondence(t1, false));
			Assert.assertEquals(t1, isomorphism.getVertexCorrespondence(t1, true));
			Assert.assertEquals(t2, isomorphism.getVertexCorrespondence(t2, false));
			Assert.assertEquals(t2, isomorphism.getVertexCorrespondence(t2, true));
			
			Assert.assertEquals(c1, isomorphism.getVertexCorrespondence(c1, false));
			Assert.assertEquals(c1, isomorphism.getVertexCorrespondence(c1, true));
			Assert.assertEquals(c12, isomorphism.getVertexCorrespondence(c12, false));
			Assert.assertEquals(c12, isomorphism.getVertexCorrespondence(c12, true));
			Assert.assertEquals(c2, isomorphism.getVertexCorrespondence(c2, false));
			Assert.assertEquals(c2, isomorphism.getVertexCorrespondence(c2, true));
			Assert.assertEquals(c22, isomorphism.getVertexCorrespondence(c22, false));
			Assert.assertEquals(c22, isomorphism.getVertexCorrespondence(c22, true));
		}
	}
	
	@Test
	public void NewTableIsDetectedCorrectly()  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema5);
		
		Assert.assertFalse(comparer1.isIsomorphic());
		Assert.assertNotNull(comparer1.matching);
		Assert.assertNotNull(comparer1.comparisonResult);
		
		Assert.assertNotNull(comparer1.comparisonResult.getAddedTable());
		Assert.assertEquals(t2.getSqlElementId(), comparer1.comparisonResult.getAddedTable().getSqlElementId());
	}
	
	@Test
	public void RemovedTableIsDetectedCorrectly()  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema8);
		
		Assert.assertFalse(comparer1.isIsomorphic());
		Assert.assertNotNull(comparer1.matching);
		Assert.assertNotNull(comparer1.comparisonResult);
		
		Assert.assertNotNull(comparer1.comparisonResult.getRemovedTable());
		Assert.assertEquals(t1.getSqlElementId(), comparer1.comparisonResult.getRemovedTable().getSqlElementId());
	}
	
	@Test
	public void RenamedTableIsDetectedCorrectly()  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema9);
		
		Assert.assertFalse(comparer1.isIsomorphic());
		Assert.assertNotNull(comparer1.matching);
		Assert.assertNotNull(comparer1.comparisonResult);
		
		Assert.assertNotNull(comparer1.comparisonResult.getRenamedTable());
		Assert.assertEquals(t1.getSqlElementId(), comparer1.comparisonResult.getRenamedTable().getSqlElementId());
	}
	
	@Test
	public void NewColumnIsDetectedCorrectly()  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema7, schema1);
		
		Assert.assertFalse(comparer1.isIsomorphic());
		Assert.assertNotNull(comparer1.matching);
		Assert.assertNotNull(comparer1.comparisonResult);
		
		Assert.assertNotNull(comparer1.comparisonResult.getAddedColumn());
		Assert.assertEquals(c1.getSqlElementId(), comparer1.comparisonResult.getAddedColumn().getSqlElementId());
	}
	
	@Test
	public void RemovedColumnIsDetectedCorrectly()  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema7);
		
		Assert.assertFalse(comparer1.isIsomorphic());
		Assert.assertNotNull(comparer1.matching);
		Assert.assertNotNull(comparer1.comparisonResult);
		
		Assert.assertNotNull(comparer1.comparisonResult.getRemovedColumn());
		Assert.assertEquals(c1.getSqlElementId(), comparer1.comparisonResult.getRemovedColumn().getSqlElementId());
	}
	
	@Test
	public void RenamedColumnIsDetectedCorrectly()  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema6);
		
		Assert.assertFalse(comparer1.isIsomorphic());
		Assert.assertNotNull(comparer1.matching);
		Assert.assertNotNull(comparer1.comparisonResult);
		
		Assert.assertNotNull(comparer1.comparisonResult.getRenamedColumn());
		Assert.assertEquals(c1.getSqlElementId(), comparer1.comparisonResult.getRenamedColumn().getSqlElementId());
	}
	
	@Test
	public void MovedColumnIsDetectedCorrectly()  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema4, schema5);
		
		Assert.assertFalse(comparer1.isIsomorphic());
		Assert.assertNotNull(comparer1.matching);
		Assert.assertNotNull(comparer1.comparisonResult);
		
		Assert.assertNotNull(comparer1.comparisonResult.getMovedColumn());
		Assert.assertEquals(c1.getSqlElementId(), comparer1.comparisonResult.getMovedColumn().getSqlElementId());
	}
	
	@After
	public void tearDown() {
		
	}
}
