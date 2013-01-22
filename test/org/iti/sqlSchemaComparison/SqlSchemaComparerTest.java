package org.iti.sqlSchemaComparison;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.iti.sqlSchemaComparison.edge.ForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.DefaultColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.NotNullColumnConstraint;
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
	
	private static Graph<ISqlElement, DefaultEdge> schema11 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema12 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema13 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	
	private static Graph<ISqlElement, DefaultEdge> schema31 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	
	private static ISqlElement t1 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t1");
	private static ISqlElement t2 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t2");
	
	private static ISqlElement c1 = new SqlColumnVertex("c1", "", t1.getSqlElementId());
	private static ISqlElement c12 = new SqlColumnVertex("c12", "", t1.getSqlElementId());
	private static ISqlElement c2 = new SqlColumnVertex("c2", "", t2.getSqlElementId());
	private static ISqlElement c22 = new SqlColumnVertex("c22", "", t2.getSqlElementId());
	private static ISqlElement c3 = new SqlColumnVertex("c1", "", t2.getSqlElementId());
	
	private static ISqlElement c111 = new SqlColumnVertex("c1", "INTEGER", t1.getSqlElementId());
	private static ISqlElement c112 = new SqlColumnVertex("c1", "FLOAT", t1.getSqlElementId());
	private static ISqlElement c113 = new SqlColumnVertex("c1", "INTEGER", t1.getSqlElementId());
	
	private static IColumnConstraint constraint1 = new DefaultColumnConstraint("1", c111);
	private static IColumnConstraint constraint2 = new NotNullColumnConstraint("", c112);
	
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
		schema11.addVertex(t1);
		schema12.addVertex(t1);
		schema13.addVertex(t1);
		schema31.addVertex(t1);
		schema31.addVertex(t2);
		
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
		schema5.addVertex(c2);
		schema5.addVertex(c3);
		schema6.addVertex(c2);
		schema9.addVertex(c1);
		schema11.addVertex(c111);
		schema12.addVertex(c112);
		schema13.addVertex(c113);
		schema31.addVertex(c1);
		schema31.addVertex(c12);
		schema31.addVertex(c2);
		schema31.addVertex(c22);
		
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
		
		schema5.addEdge(t2, c3, new TableHasColumnEdge(t2, c3));
		schema5.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		
		schema6.addEdge(t1, c2, new TableHasColumnEdge(t1, c2));
		
		schema9.addEdge(t2, c1, new TableHasColumnEdge(t2, c1));
		
		schema11.addEdge(t1, c111, new TableHasColumnEdge(t1, c111));
		
		schema12.addEdge(t1, c112, new TableHasColumnEdge(t1, c112));
		
		schema13.addEdge(t1, c113, new TableHasColumnEdge(t1, c113));

		List<IColumnConstraint> constraints11 = new ArrayList<IColumnConstraint>();
		List<IColumnConstraint> constraints12 = new ArrayList<IColumnConstraint>();
		
		constraints11.add(constraint1);
		
		constraints12.add(constraint1);
		constraints12.add(constraint2);
		
		((SqlColumnVertex) c111).setConstraints(constraints11);
		((SqlColumnVertex) c112).setConstraints(constraints12);
		
		schema31.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema31.addEdge(t1, c12, new TableHasColumnEdge(t1, c12));
		schema31.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		schema31.addEdge(t2, c22, new TableHasColumnEdge(t2, c22));
		schema31.addEdge(c12, c2, new ForeignKeyRelationEdge(c12, t2, c2));
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
	
	@Test
	public void UnchangedColumnAttributesDetectedCorrectly() {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema11, schema11);
		SqlSchemaColumnComparisonResult.ColumnConstraintComparisonResult cccr = comparer1.comparisonResult.getColumnComparisonResults().get(c111).getConstraintComparisonResult();		
		
		Assert.assertNotNull(comparer1.comparisonResult);
		Assert.assertEquals(1, comparer1.comparisonResult.getColumnComparisonResults().size());
		Assert.assertFalse(comparer1.comparisonResult.getColumnComparisonResults().get(c111).hasColumnTypeChanged());
		Assert.assertEquals(0, cccr.getAddedConstraints().size());
		Assert.assertEquals(0, cccr.getRemovedConstraints().size());
	}
	
	@Test
	public void ChangedColumnTypeDetectedCorrectly() {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema11, schema12);		
		
		Assert.assertTrue(comparer1.comparisonResult.getColumnComparisonResults().get(c112).hasColumnTypeChanged());
	}
	
	@Test
	public void AddedColumnConstraintDetectedCorrectly() {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema11, schema12);		
		SqlSchemaColumnComparisonResult.ColumnConstraintComparisonResult cccr = comparer1.comparisonResult.getColumnComparisonResults().get(c112).getConstraintComparisonResult();
		
		Assert.assertTrue(cccr.getAddedConstraints().contains(constraint2));
	}
	
	@Test
	public void RemovedColumnConstraintDetectedCorrectly() {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema11, schema13);		
		SqlSchemaColumnComparisonResult.ColumnConstraintComparisonResult cccr = comparer1.comparisonResult.getColumnComparisonResults().get(c113).getConstraintComparisonResult();
		
		Assert.assertTrue(cccr.getRemovedConstraints().contains(constraint1));
	}
	
	@After
	public void tearDown() {
		
	}
}
