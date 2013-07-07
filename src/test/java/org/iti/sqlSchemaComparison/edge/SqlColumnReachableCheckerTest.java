package org.iti.sqlSchemaComparison.edge;

import junit.framework.Assert;

import org.iti.sqlSchemaComparison.reachability.ISqlElementReachabilityChecker;
import org.iti.sqlSchemaComparison.reachability.SqlColumnReachableChecker;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqlColumnReachableCheckerTest {

	private static Graph<ISqlElement, DefaultEdge> schema1 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema2 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	private static Graph<ISqlElement, DefaultEdge> schema3 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	
	private static ISqlElement t1 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t1");
	private static ISqlElement t2 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t2");
	private static ISqlElement t3 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t3");
	
	private static ISqlElement c1 = new SqlColumnVertex("c1", "", t1.getSqlElementId());
	private static ISqlElement c2 = new SqlColumnVertex("c2", "", t1.getSqlElementId());
	private static ISqlElement c3 = new SqlColumnVertex("c121", "", t1.getSqlElementId());
	private static ISqlElement c4 = new SqlColumnVertex("c3", "", t1.getSqlElementId());
	private static ISqlElement c5 = new SqlColumnVertex("c231", "", t1.getSqlElementId());
	
	@BeforeClass
	public static void Init() throws Exception {
		schema1.addVertex(t1);
		schema1.addVertex(t2);
		schema2.addVertex(t1);
		schema2.addVertex(t2);
		schema3.addVertex(t1);
		schema3.addVertex(t2);
		schema3.addVertex(t3);

		schema1.addVertex(c1);
		schema1.addVertex(c2);
		schema2.addVertex(c1);
		schema2.addVertex(c2);
		schema2.addVertex(c3);
		schema3.addVertex(c1);
		schema3.addVertex(c2);
		schema3.addVertex(c3);
		schema3.addVertex(c4);
		schema3.addVertex(c5);
		
		schema1.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema1.addEdge(t2, c2, new TableHasColumnEdge(t1, c2));
		
		schema2.addEdge(t1, c3, new TableHasColumnEdge(t1, c3));
		schema2.addEdge(t2, c1, new TableHasColumnEdge(t2, c1));
		schema2.addEdge(t2, c2, new TableHasColumnEdge(t1, c2));
		schema2.addEdge(c3, c2, new ForeignKeyRelationEdge(c3, t2, c2));
		
		schema3.addEdge(t1, c3, new TableHasColumnEdge(t1, c3));
		schema3.addEdge(t2, c5, new TableHasColumnEdge(t2, c5));
		schema3.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		schema3.addEdge(t3, c4, new TableHasColumnEdge(t3, c4));
		schema3.addEdge(t3, c1, new TableHasColumnEdge(t3, c1));
		schema3.addEdge(c3, c2, new ForeignKeyRelationEdge(c3, t2, c2));
		schema3.addEdge(c5, c4, new ForeignKeyRelationEdge(c5, t3, c4));
	}
	
	@Before
	public void setUp() { }
	
	@Test
	public void ReachableColumnDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlColumnReachableChecker(schema1, t1, c1);
		
		Assert.assertTrue(checker.isReachable());
		Assert.assertEquals(2, checker.getPath().size());
		Assert.assertTrue(checker.getPath().contains(t1));
		Assert.assertTrue(checker.getPath().contains(c1));
	}
	
	@Test
	public void ReachableForeignColumnDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlColumnReachableChecker(schema2, t1, c1);
		
		Assert.assertTrue(checker.isReachable());
		Assert.assertEquals(5, checker.getPath().size());
		Assert.assertTrue(checker.getPath().contains(t1));
		Assert.assertTrue(checker.getPath().contains(c3));
		Assert.assertTrue(checker.getPath().contains(c2));
		Assert.assertTrue(checker.getPath().contains(t2));
		Assert.assertTrue(checker.getPath().contains(c1));
	}
	
	@Test
	public void ReachableForeignColumnWithOneIndirectionDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlColumnReachableChecker(schema3, t1, c1);
		
		Assert.assertTrue(checker.isReachable());
		Assert.assertEquals(8, checker.getPath().size());
		Assert.assertTrue(checker.getPath().contains(t1));
		Assert.assertTrue(checker.getPath().contains(c3));
		Assert.assertTrue(checker.getPath().contains(c2));
		Assert.assertTrue(checker.getPath().contains(t2));
		Assert.assertTrue(checker.getPath().contains(c5));
		Assert.assertTrue(checker.getPath().contains(c4));
		Assert.assertTrue(checker.getPath().contains(t3));
		Assert.assertTrue(checker.getPath().contains(c1));
	}
	
	@After
	public void tearDown() {
		
	}
}
