package org.iti.sqlSchemaComparison.edge;

import junit.framework.Assert;

import org.iti.sqlSchemaComparison.reachability.ISqlElementReachabilityChecker;
import org.iti.sqlSchemaComparison.reachability.SqlColumnReachableChecker;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
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
	
	private static ISqlElement t1 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t1");
	private static ISqlElement t2 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t2");
	
	private static ISqlElement c1 = SqlElementFactory.createSqlElement(SqlElementType.Column, "c1");
	private static ISqlElement c2 = SqlElementFactory.createSqlElement(SqlElementType.Column, "c2");
	private static ISqlElement c3 = SqlElementFactory.createSqlElement(SqlElementType.Column, "c121");
	
	@BeforeClass
	public static void Init() throws Exception {
		schema1.addVertex(t1);
		schema1.addVertex(t2);
		schema2.addVertex(t1);
		schema2.addVertex(t2);

		schema1.addVertex(c1);
		schema1.addVertex(c2);
		schema2.addVertex(c1);
		schema2.addVertex(c2);
		schema2.addVertex(c3);
		
		schema1.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema1.addEdge(t2, c2, new TableHasColumnEdge(t1, c2));
		
		schema2.addEdge(t1, c3, new TableHasColumnEdge(t1, c3));
		schema2.addEdge(t2, c1, new TableHasColumnEdge(t2, c1));
		schema2.addEdge(t2, c2, new TableHasColumnEdge(t1, c2));
		schema2.addEdge(c3, t2, new ForeignKeyRelationEdge(c3, t2, c2));
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
		Assert.assertEquals(4, checker.getPath().size());
		Assert.assertTrue(checker.getPath().contains(t1));
		Assert.assertTrue(checker.getPath().contains(c3));
		Assert.assertTrue(checker.getPath().contains(t2));
		Assert.assertTrue(checker.getPath().contains(c1));
	}
	
	@After
	public void tearDown() {
		
	}
}
