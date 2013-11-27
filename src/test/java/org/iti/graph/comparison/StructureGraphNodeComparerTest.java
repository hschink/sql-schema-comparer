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
import org.iti.graph.comparison.StructureGraphComparer.AmbiguousMoveException;
import org.iti.graph.comparison.StructureGraphComparer.AmbiguousRenameException;
import org.iti.graph.comparison.result.IModificationDetail;
import org.iti.graph.comparison.result.StructureGraphComparisonResult;
import org.iti.graph.comparison.result.Type;
import org.iti.graph.helper.Edge2;
import org.iti.graph.helper.Edge6;
import org.iti.graph.helper.Element;
import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StructureGraphNodeComparerTest {

	private static IStructureGraphComparer comparer = new StructureGraphComparer();

	private static StructureGraph structureGraphOriginal;
	
	private DirectedGraph<IStructureElement, DefaultEdge> currentGraph;

	private Map<String, Type> expectedModifications = new HashMap<>();
	private Map<String, IModificationDetail> expectedModificationDetails = new HashMap<>();

	private StructureGraphComparisonResult result;

	@BeforeClass
	public static void init() throws Exception {
		structureGraphOriginal = StructureGraphComparerTestHelper.getOriginal();
	}

	@Before
	public void setUp() throws Exception {
		currentGraph = StructureGraphComparerTestHelper.getCurrentGraph();

		expectedModifications.clear();
		expectedModificationDetails.clear();

		result = null;
	}

	@Test
	public void detectsRemovedNodes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenRemovedNodes(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedNodeRemovals(structureGraphOriginal, expectedModifications);
		StructureGraphComparerTestHelper.givenExpectedNodeRemovalDetails(structureGraphOriginal, expectedModificationDetails);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getNodeModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
		StructureGraphComparerTestHelper.assertNodeModificationDetailExpectations(expectedModificationDetails, result);
	}

	@Test
	public void detectsAddedNodes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenAddedNodes(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedNodeAddition(currentGraph, expectedModifications);
		StructureGraphComparerTestHelper.givenExpectAdditionDetails(currentGraph, expectedModificationDetails);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getNodeModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
		StructureGraphComparerTestHelper.assertNodeModificationDetailExpectations(expectedModificationDetails, result);
	}

	@Test
	public void detectsAddedAndRemovedNodes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenRemovedNodes(currentGraph);
		StructureGraphComparerTestHelper.givenAddedNodes(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedNodeAddition(currentGraph, expectedModifications);
		StructureGraphComparerTestHelper.givenExpectedNodeRemovals(structureGraphOriginal, expectedModifications);
		StructureGraphComparerTestHelper.givenExpectAdditionDetails(currentGraph, expectedModificationDetails);
		StructureGraphComparerTestHelper.givenExpectedNodeRemovalDetails(structureGraphOriginal, expectedModificationDetails);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getNodeModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
		StructureGraphComparerTestHelper.assertNodeModificationDetailExpectations(expectedModificationDetails, result);
	}

	@Test
	public void detectsRenamedNodes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenRenamedNode(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedNodeRename(expectedModifications);
		StructureGraphComparerTestHelper.givenExpectRenameDetail(expectedModificationDetails);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getNodeModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
		StructureGraphComparerTestHelper.assertNodeModificationDetailExpectations(expectedModificationDetails, result);
	}

	@Test
	public void detectsMovedNodes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenMovedNode(currentGraph);
		StructureGraphComparerTestHelper.givenExpectMove(expectedModifications);
		StructureGraphComparerTestHelper.givenExpectMoveDetail(expectedModificationDetails);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getNodeModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
		StructureGraphComparerTestHelper.assertNodeModificationDetailExpectations(expectedModificationDetails, result);
	}

	@Test
	public void detectsRenamedMovedNodes() throws StructureGraphComparisonException {
		StructureGraphComparerTestHelper.givenRenamedNode(currentGraph);
		StructureGraphComparerTestHelper.givenMovedNode(currentGraph);
		StructureGraphComparerTestHelper.givenExpectedNodeRename(expectedModifications);
		StructureGraphComparerTestHelper.givenExpectRenameDetail(expectedModificationDetails);
		StructureGraphComparerTestHelper.givenExpectMove(expectedModifications);
		StructureGraphComparerTestHelper.givenExpectMoveDetail(expectedModificationDetails);

		whenComparisonResultIsCreated();

		assertEquals(expectedModifications.size(), result.getNodeModifications().size());

		StructureGraphComparerTestHelper.assertModificationExpectations(expectedModifications, result);
	}
	
	@Test(expected=AmbiguousRenameException.class)
	public void throwsAmbiguousRenameException() throws StructureGraphComparisonException {
		Element renamedElement1 = new Element("cn3r1");
		Element renamedElement2 = new Element("cn3r2");

		currentGraph.removeVertex(StructureGraphComparerTestHelper.cn3);
		currentGraph.addVertex(renamedElement1);
		currentGraph.addVertex(renamedElement2);

		currentGraph.addEdge(StructureGraphComparerTestHelper.cn1, renamedElement1, new Edge2());
		currentGraph.addEdge(StructureGraphComparerTestHelper.cn1, renamedElement2, new Edge2());

		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		comparer.compare(structureGraphOriginal, structureGraphCurrent);
	}

	@Test(expected=AmbiguousMoveException.class)
	public void throwsAmbiguousMoveException() throws StructureGraphComparisonException {
		Element movedElement = new Element("cn6");

		currentGraph.addVertex(movedElement);

		currentGraph.removeEdge(StructureGraphComparerTestHelper.cn2, StructureGraphComparerTestHelper.cn6);

		currentGraph.addEdge(StructureGraphComparerTestHelper.cn4, StructureGraphComparerTestHelper.cn6, new Edge6());
		currentGraph.addEdge(StructureGraphComparerTestHelper.cn3, movedElement, new Edge6());

		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		comparer.compare(structureGraphOriginal, structureGraphCurrent);
	}

	private void whenComparisonResultIsCreated() throws StructureGraphComparisonException {
		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		result = comparer.compare(structureGraphOriginal, structureGraphCurrent);
	}
}
