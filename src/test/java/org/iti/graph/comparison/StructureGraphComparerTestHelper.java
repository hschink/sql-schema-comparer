package org.iti.graph.comparison;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.Map;
import java.util.Map.Entry;

import org.iti.graph.StructureGraph;
import org.iti.graph.comparison.result.IModificationDetail;
import org.iti.graph.comparison.result.OriginalStructureElement;
import org.iti.graph.comparison.result.StructureGraphComparisonResult;
import org.iti.graph.comparison.result.Type;
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

public class StructureGraphComparerTestHelper {

	static Element re = new Element("re");
	static Element cn1 = new Element("cn1");
	static Element cn2 = new Element("cn2");
	static Element cn3 = new Element("cn3");
	static Element cn4 = new Element("cn4");
	static Element cn5 = new Element("cn5");
	static Element cn6 = new Element("cn6");
	static Element cn7 = new Element("cn7");
	static Element cn8 = new Element("cn8");

	static StructureGraph getOriginal() {
		DirectedGraph<IStructureElement, DefaultEdge> originalGraph = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);

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

		return new StructureGraph(originalGraph);
	}

	static SimpleDirectedGraph<IStructureElement, DefaultEdge> getCurrentGraph() {
		SimpleDirectedGraph<IStructureElement, DefaultEdge> currentGraph = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);

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

		return currentGraph;
	}

	static void givenRemovedNodes(DirectedGraph<IStructureElement, DefaultEdge> currentGraph) {
		currentGraph.removeVertex(cn3);
		currentGraph.removeVertex(cn2);
		currentGraph.removeVertex(cn5);
		currentGraph.removeVertex(cn6);
	}

	static void givenAddedNodes(DirectedGraph<IStructureElement, DefaultEdge> currentGraph) {
		currentGraph.addVertex(cn7);
		currentGraph.addVertex(cn8);

		currentGraph.addEdge(cn1, cn7, new Edge7());
		currentGraph.addEdge(re, cn8, new Edge8());
	}

	static IStructureElement givenRenamedNode(DirectedGraph<IStructureElement, DefaultEdge> currentGraph) {
		Element renamedElement = new Element("cn3r");

		currentGraph.removeVertex(cn3);
		currentGraph.addVertex(renamedElement);

		currentGraph.addEdge(cn1, renamedElement, new Edge2());

		return renamedElement;
	}

	static void givenMovedNode(DirectedGraph<IStructureElement, DefaultEdge> currentGraph) {
		currentGraph.removeEdge(cn2, cn6);
		currentGraph.addEdge(cn4, cn6, new Edge6());
	}

	static void givenExpectedNodeRemovals(StructureGraph originalGraph,
			Map<String, Type> expectedModifications) {
		givenExpectedNodeRemovals(cn3, originalGraph, expectedModifications);
		givenExpectedNodeRemovals(cn2, originalGraph, expectedModifications);
		givenExpectedNodeRemovals(cn5, originalGraph, expectedModifications);
		givenExpectedNodeRemovals(cn6, originalGraph, expectedModifications);
	}

	static void givenExpectedNodeRemovals(IStructureElement element,
			StructureGraph originalGraph,
			Map<String, Type> expectedModifications) {

		expectedModifications.put(originalGraph.getIdentifier(element), Type.NodeDeleted);
	}

	static void givenExpectedNodeRemovalDetails(StructureGraph originalGraph,
			Map<String, IModificationDetail> expectedModificationDetails) {
		expectedModificationDetails.put(originalGraph.getIdentifier(cn3), null);
		expectedModificationDetails.put(originalGraph.getIdentifier(cn2), null);
		expectedModificationDetails.put(originalGraph.getIdentifier(cn5), null);
		expectedModificationDetails.put(originalGraph.getIdentifier(cn6), null);
	}

	static void givenExpectedPathRemovals(StructureGraph originalGraph,
			Map<String, Type> expectedModifications) {
		givenExpectedPathRemovals("cn1.Edge2", expectedModifications);
		givenExpectedPathRemovals("re.Edge4", expectedModifications);
		givenExpectedPathRemovals("cn2.Edge5", expectedModifications);
		givenExpectedPathRemovals("cn2.Edge6", expectedModifications);
	}

	static void givenExpectedPathRemovals(String path,
			Map<String, Type> expectedModifications) {

		expectedModifications.put(path, Type.PathDeleted);
	}

	static void givenExpectedNodeAddition(DirectedGraph<IStructureElement, DefaultEdge> currentGraph,
			Map<String, Type> expectedModifications) {
		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		givenExpectedNodeAddition(cn7, structureGraphCurrent, expectedModifications);
		givenExpectedNodeAddition(cn8, structureGraphCurrent, expectedModifications);
	}

	static void givenExpectedNodeAddition(IStructureElement element,
			StructureGraph currentGraph,
			Map<String, Type> expectedModifications) {

		expectedModifications.put(currentGraph.getIdentifier(element), Type.NodeAdded);
	}

	static void givenExpectedPathAddition(DirectedGraph<IStructureElement, DefaultEdge> currentGraph,
			Map<String, Type> expectedModifications) {
		givenExpectedPathAddition("cn1.Edge7", expectedModifications);
		givenExpectedPathAddition("re.Edge8", expectedModifications);
	}

	static void givenExpectedPathAddition(String path,
			Map<String, Type> expectedModifications) {

		expectedModifications.put(path, Type.PathAdded);
	}

	static void givenExpectAdditionDetails(DirectedGraph<IStructureElement, DefaultEdge> currentGraph,
			Map<String, IModificationDetail> expectedModificationDetails) {
		StructureGraph structureGraphCurrent = new StructureGraph(currentGraph);

		expectedModificationDetails.put(structureGraphCurrent.getIdentifier(cn7), null);
		expectedModificationDetails.put(structureGraphCurrent.getIdentifier(cn8), null);
	}

	static void givenExpectedNodeRename(Map<String, Type> expectedModifications) {
		expectedModifications.put("re.Edge1(cn1.Edge2(cn3r))", Type.NodeRenamed);
	}

	static void givenExpectRenameDetail(Map<String, IModificationDetail> expectedModificationDetails) {
		expectedModificationDetails.put("re.Edge1(cn1.Edge2(cn3r))",
				new OriginalStructureElement("re.Edge1(cn1.Edge2(cn3))"));
	}

	static void givenExpectMove(Map<String, Type> expectedModifications) {
		expectedModifications.put("re.Edge1(cn1.Edge3(cn4.Edge6(cn6)))", Type.NodeMoved);
	}

	static IModificationDetail givenExpectMoveDetail(Map<String, IModificationDetail> expectedModificationDetails) {
		return expectedModificationDetails.put("re.Edge1(cn1.Edge3(cn4.Edge6(cn6)))",
				new OriginalStructureElement("re.Edge4(cn2.Edge6(cn6))"));
	}

	public static void givenExpectPathRename( Map<String, Type> expectedModifications) {
		expectedModifications.put("cn4.Edge6", Type.PathRenamed);
	}

	public static IModificationDetail givenExpectPathRenameDetail(
			Map<String, IModificationDetail> expectedModificationDetails) {
		return expectedModificationDetails.put("cn4.Edge6",
				new OriginalStructureElement("cn2.Edge6"));
	}

	static void assertModificationExpectations(
			Map<String, Type> expectedModifications,
			StructureGraphComparisonResult result) {
		for (Entry<String, Type> expectation : expectedModifications.entrySet()) {
			assertNotNull(result.getModifications().get(expectation.getKey()));
			assertEquals(expectation.getKey(),
					expectation.getValue(),
					result.getModifications().get(expectation.getKey()).getType());
		}
	}

	public static void assertNodeModificationDetailExpectations(
			Map<String, IModificationDetail> expectedModificationDetails,
			StructureGraphComparisonResult result) {
		for (Entry<String, IModificationDetail> expectation : expectedModificationDetails.entrySet()) {
			IModificationDetail detail = expectation.getValue();
			IModificationDetail actualDetail = result.getModifications().get(expectation.getKey()).getModificationDetail();

			if (detail == null) {
				assertNull(expectation.getKey(), actualDetail);;
			} else {
				assertEquals(expectation.getKey(), detail.getIdentifier(), detail.getIdentifier());
			}
		}
	}
}
