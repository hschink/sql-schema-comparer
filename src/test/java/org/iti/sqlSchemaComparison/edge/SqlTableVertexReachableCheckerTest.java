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
	public static void init() throws Exception {
		schema1.addVertex(t1);

		schema1.addVertex(c1);
		schema1.addVertex(c12);
		
		schema1.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema1.addEdge(t1, c12, new TableHasColumnEdge(t1, c12));
	}
	
	@Before
	public void setUp() { }
	
	@Test
	public void reachableTableDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlTableVertexReachableChecker(schema1, t1);
		
		assertTrue(checker.isReachable());
		assertEquals(1, checker.getPath().size());
		assertTrue(checker.getPath().contains(t1));
	}
	
	@Test
	public void nonReachableTableDetectedCorrectly() {
		ISqlElementReachabilityChecker checker = new SqlTableVertexReachableChecker(schema1, t2);
		
		assertFalse(checker.isReachable());
		assertEquals(0, checker.getPath().size());
		assertFalse(checker.getPath().contains(t2));
	}
	
	@After
	public void tearDown() {
		
	}
}
