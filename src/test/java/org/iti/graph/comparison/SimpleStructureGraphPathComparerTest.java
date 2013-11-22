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

package org.iti.graph.comparison;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.iti.graph.StructureGraph;
import org.iti.graph.comparison.result.StructureGraphComparisonResult;
import org.iti.graph.comparison.result.Type;
import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleStructureGraphPathComparerTest {

	private static IStructureGraphComparer comparer = new SimpleStructureGraphComparer();

	private static StructureGraph structureGraphOriginal;
	
	private DirectedGraph<IStructureElement, DefaultEdge> currentGraph;

	private Map<String, Type> expectedModifications = new HashMap<>();

	private StructureGraphComparisonResult result;
	
	@BeforeClass
	public static void init() throws Exception {
		structureGraphOriginal = StructureGraphComparerTestHelper.getOriginal();
	}

	@Before
	public void setUp() throws Exception {
		currentGraph = StructureGraphComparerTestHelper.getCurrentGraph();

		expectedModifications.clear();

		result = null;
	}

	@Test
	public void detectsRemovedPathes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenRemovedNodes(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedPathRemovals(structureGraphOriginal, expectedModifications);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getPathModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
	}

	@Test
	public void detectsAddedPathes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenAddedNodes(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedPathAddition(currentGraph, expectedModifications);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getPathModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
	}

	@Test
	public void detectsAddedAndRemovedPathes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenRemovedNodes(currentGraph);
		StructureGraphComparerTestHelper.givenAddedNodes(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedPathRemovals(structureGraphOriginal, expectedModifications);
		StructureGraphComparerTestHelper.givenExpectedPathAddition(currentGraph, expectedModifications);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getPathModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
	}

	@Test
	public void detectsRenamedPathes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenRenamedNode(currentGraph);

		whenComparisonResultIsCreated();

		assertEquals(0, result.getPathModifications().size());
	}

	@Test
	public void detectsMovedPathes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenMovedNode(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedPathRemovals("cn2.Edge6", expectedModifications);
		StructureGraphComparerTestHelper.givenExpectedPathAddition("cn4.Edge6", expectedModifications);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getPathModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
	}

	private void whenComparisonResultIsCreated()
			throws StructureGraphComparisonException {
		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		result = comparer.compare(structureGraphOriginal, structureGraphCurrent);
	}
}
