package org.iti.sqlSchemaComparison.edge;

import static org.junit.Assert.*;

import org.iti.sqlSchemaComparison.reachability.ISqlElementReachabilityChecker;
import org.iti.sqlSchemaComparison.reachability.SqlTableVertexReachableChecker;
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
public class SqlTableVertexReachableCheckerTest {

	private static Graph<ISqlElement, DefaultEdge> schema1 = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
	
	private static ISqlElement t1 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t1");
	private static ISqlElement t2 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t2");
	
	private static ISqlElement c1 = new SqlColumnVertex("c1", "", t1.getSqlElementId());
	private static ISqlElement c12 = new SqlColumnVertex("c12", "", t2.getSqlElementId());
	
	@BeforeClass
	public static void Init() throws Exception {
		schema1.addVertex(t1);

		schema1.addVertex(c1);
		schema1.addVertex(c12);
		
		schema1.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema1.addEdge(t1, c12, new TableHasColumnEdge(t1, c12));
	}
	
	@Before
	public void setUp() { }
	
	@Test
	public void ReachableTableDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlTableVertexReachableChecker(schema1, t1);
		
		assertTrue(checker.isReachable());
		assertEquals(1, checker.getPath().size());
		assertTrue(checker.getPath().contains(t1));
	}
	
	@Test
	public void NonReachableTableDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlTableVertexReachableChecker(schema1, t2);
		
		assertFalse(checker.isReachable());
		assertEquals(0, checker.getPath().size());
		assertFalse(checker.getPath().contains(t2));
	}
	
	@After
	public void tearDown() {
		
	}
}
