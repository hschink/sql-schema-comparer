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
import java.util.Map.Entry;

import org.iti.graph.StructureGraph;
import org.iti.graph.helper.Edge1;
import org.iti.graph.helper.Edge2;
import org.iti.graph.helper.Edge3;
import org.iti.graph.helper.Edge4;
import org.iti.graph.helper.Edge5;
import org.iti.graph.helper.Edge6;
import org.iti.graph.helper.Edge7;
import org.iti.graph.helper.Edge8;
import org.iti.graph.helper.Element;
import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleStructureGraphComparerTest {

	private static IStructureGraphComparer comparer;

	private static StructureGraph structureGraphOriginal;
	
	private static Element re = new Element("re");
	private static Element cn1 = new Element("cn1");
	private static Element cn2 = new Element("cn2");
	private static Element cn3 = new Element("cn3");
	private static Element cn4 = new Element("cn4");
	private static Element cn5 = new Element("cn5");
	private static Element cn6 = new Element("cn6");
	private static Element cn7 = new Element("cn7");
	private static Element cn8 = new Element("cn8");
	
	private static DirectedGraph<IStructureElement, DefaultEdge> originalGraph;

	private DirectedGraph<IStructureElement, DefaultEdge> currentGraph;
	
	@BeforeClass
	public static void init() throws Exception {
		comparer = new SimpleStructureGraphComparer();

		originalGraph = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
		
		originalGraph.addVertex(re);
		originalGraph.addVertex(cn1);
		originalGraph.addVertex(cn2);
		originalGraph.addVertex(cn3);
		originalGraph.addVertex(cn4);
		originalGraph.addVertex(cn5);
		originalGraph.addVertex(cn6);
		
		originalGraph.addEdge(re, cn1, new Edge1());
		originalGraph.addEdge(cn1, cn3, new Edge2());
		originalGraph.addEdge(cn1, cn4, new Edge3());
		originalGraph.addEdge(re, cn2, new Edge4());
		originalGraph.addEdge(cn2, cn5, new Edge5());
		originalGraph.addEdge(cn2, cn6, new Edge6());

		structureGraphOriginal = new StructureGraph(originalGraph);
	}

	@Before
	public void setUp() throws Exception {
		currentGraph = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);

		currentGraph.addVertex(re);
		currentGraph.addVertex(cn1);
		currentGraph.addVertex(cn2);
		currentGraph.addVertex(cn3);
		currentGraph.addVertex(cn4);
		currentGraph.addVertex(cn5);
		currentGraph.addVertex(cn6);
		
		currentGraph.addEdge(re, cn1, new Edge1());
		currentGraph.addEdge(cn1, cn3, new Edge2());
		currentGraph.addEdge(cn1, cn4, new Edge3());
		currentGraph.addEdge(re, cn2, new Edge4());
		currentGraph.addEdge(cn2, cn5, new Edge5());
		currentGraph.addEdge(cn2, cn6, new Edge6());
	}

	@Test
	public void detectsRemovedNodes() {
		Map<IStructureElement, StructureElementModification> expectedModifications = new HashMap<>();
		expectedModifications.put(cn3, StructureElementModification.NodeDeleted);
		expectedModifications.put(cn2, StructureElementModification.NodeDeleted);
		expectedModifications.put(cn5, StructureElementModification.NodeDeleted);
		expectedModifications.put(cn6, StructureElementModification.NodeDeleted);

		currentGraph.removeVertex(cn3);
		currentGraph.removeVertex(cn2);
		currentGraph.removeVertex(cn5);
		currentGraph.removeVertex(cn6);

		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		StructureGraphComparisonResult result = comparer.compare(structureGraphOriginal, structureGraphCurrent);

		assertEquals(4, result.getModifications().size());

		assertNodeModificationExpectations(expectedModifications, result);
	}

	@Test
	public void detectsAddedNodes() {
		Map<IStructureElement, StructureElementModification> expectedModifications = new HashMap<>();
		expectedModifications.put(cn7, StructureElementModification.NodeAdded);
		expectedModifications.put(cn8, StructureElementModification.NodeAdded);

		currentGraph.addVertex(cn7);
		currentGraph.addVertex(cn8);

		currentGraph.addEdge(cn1, cn7, new Edge7());
		currentGraph.addEdge(re, cn8, new Edge8());

		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		StructureGraphComparisonResult result = comparer.compare(structureGraphOriginal, structureGraphCurrent);

		assertEquals(2, result.getModifications().size());

		assertNodeModificationExpectations(expectedModifications, result);
	}

	@Test
	public void detectsAddedAndRemovedNodes() {
		Map<IStructureElement, StructureElementModification> expectedModifications = new HashMap<>();
		expectedModifications.put(cn3, StructureElementModification.NodeDeleted);
		expectedModifications.put(cn2, StructureElementModification.NodeDeleted);
		expectedModifications.put(cn5, StructureElementModification.NodeDeleted);
		expectedModifications.put(cn6, StructureElementModification.NodeDeleted);
		expectedModifications.put(cn7, StructureElementModification.NodeAdded);
		expectedModifications.put(cn8, StructureElementModification.NodeAdded);

		currentGraph.removeVertex(cn3);
		currentGraph.removeVertex(cn2);
		currentGraph.removeVertex(cn5);
		currentGraph.removeVertex(cn6);

		currentGraph.addVertex(cn7);
		currentGraph.addVertex(cn8);

		currentGraph.addEdge(cn1, cn7, new Edge7());
		currentGraph.addEdge(re, cn8, new Edge8());

		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		StructureGraphComparisonResult result = comparer.compare(structureGraphOriginal, structureGraphCurrent);

		assertEquals(6, result.getModifications().size());

		assertNodeModificationExpectations(expectedModifications, result);
	}

	private static void assertNodeModificationExpectations(
			Map<IStructureElement, StructureElementModification> expectedModifications,
			StructureGraphComparisonResult result) {
		for (Entry<IStructureElement, StructureElementModification> expectation : expectedModifications.entrySet()) {
			assertEquals(expectation.getValue(), result.getModifications().get(expectation.getKey()));
			
		}
	}
}
