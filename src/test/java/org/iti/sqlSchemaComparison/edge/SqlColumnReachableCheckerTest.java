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

package org.iti.sqlSchemaComparison.edge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.iti.sqlSchemaComparison.reachability.ISqlElementReachabilityChecker;
import org.iti.sqlSchemaComparison.reachability.SqlColumnReachableChecker;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqlColumnReachableCheckerTest {

	private static DirectedGraph<IStructureElement, DefaultEdge> schema1 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema2 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema3 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	
	private static ISqlElement t1 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t1");
	private static ISqlElement t2 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t2");
	private static ISqlElement t3 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t3");
	
	private static ISqlElement c1 = new SqlColumnVertex("c1", "", t1.getSqlElementId());
	private static ISqlElement c2 = new SqlColumnVertex("c2", "", t1.getSqlElementId());
	private static ISqlElement c3 = new SqlColumnVertex("c121", "", t1.getSqlElementId());
	private static ISqlElement c4 = new SqlColumnVertex("c3", "", t1.getSqlElementId());
	private static ISqlElement c5 = new SqlColumnVertex("c231", "", t1.getSqlElementId());
	
	@BeforeClass
	public static void init() throws Exception {
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
		schema1.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		
		schema2.addEdge(t1, c3, new TableHasColumnEdge(t1, c3));
		schema2.addEdge(t2, c1, new TableHasColumnEdge(t2, c1));
		schema2.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
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
	public void reachableColumnDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlColumnReachableChecker(schema1, t1, c1);
		
		assertTrue(checker.isReachable());
		assertEquals(2, checker.getPath().size());
		assertTrue(checker.getPath().contains(t1));
		assertTrue(checker.getPath().contains(c1));
	}
	
	@Test
	public void reachableForeignColumnDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlColumnReachableChecker(schema2, t1, c1);
		
		assertTrue(checker.isReachable());
		assertEquals(5, checker.getPath().size());
		assertTrue(checker.getPath().contains(t1));
		assertTrue(checker.getPath().contains(c3));
		assertTrue(checker.getPath().contains(c2));
		assertTrue(checker.getPath().contains(t2));
		assertTrue(checker.getPath().contains(c1));
	}
	
	@Test
	public void reachableForeignColumnWithOneIndirectionDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlColumnReachableChecker(schema3, t1, c1);
		
		assertTrue(checker.isReachable());
		assertEquals(8, checker.getPath().size());
		assertTrue(checker.getPath().contains(t1));
		assertTrue(checker.getPath().contains(c3));
		assertTrue(checker.getPath().contains(c2));
		assertTrue(checker.getPath().contains(t2));
		assertTrue(checker.getPath().contains(c5));
		assertTrue(checker.getPath().contains(c4));
		assertTrue(checker.getPath().contains(t3));
		assertTrue(checker.getPath().contains(c1));
	}
	
	@After
	public void tearDown() {
		
	}
}
