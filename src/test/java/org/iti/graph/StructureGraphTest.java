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

package org.iti.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.iti.graph.helper.Edge1;
import org.iti.graph.helper.Edge2;
import org.iti.graph.helper.Edge3;
import org.iti.graph.helper.Edge4;
import org.iti.graph.helper.Edge5;
import org.iti.graph.helper.Edge6;
import org.iti.graph.helper.Element;
import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StructureGraphTest {

	private static StructureGraph structureGraph;
	
	private static Element re = new Element("re");
	private static Element cn1 = new Element("cn1");
	private static Element cn2 = new Element("cn2");
	private static Element cn3 = new Element("cn3");
	private static Element cn4 = new Element("cn4");
	private static Element cn5 = new Element("cn5");
	private static Element cn6 = new Element("cn6");
	
	private static DirectedGraph<IStructureElement, DefaultEdge> graph1;
	
	@BeforeClass
	public static void init() throws Exception {
		graph1 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
		
		graph1.addVertex(re);
		graph1.addVertex(cn1);
		graph1.addVertex(cn2);
		graph1.addVertex(cn3);
		graph1.addVertex(cn4);
		graph1.addVertex(cn5);
		graph1.addVertex(cn6);
		
		graph1.addEdge(re, cn1, new Edge1());
		graph1.addEdge(cn1, cn3, new Edge2());
		graph1.addEdge(cn1, cn4, new Edge3());
		graph1.addEdge(re, cn2, new Edge4());
		graph1.addEdge(cn2, cn5, new Edge5());
		graph1.addEdge(cn2, cn6, new Edge6());
		
		structureGraph = new StructureGraph(graph1);
	}

	@Test
	public void getStructureElement() {
		Map<IStructureElement, String> identifiers = new HashMap<>();
		identifiers.put(re, "re");
		identifiers.put(cn1, "re.Edge1(cn1)");
		identifiers.put(cn2, "re.Edge4(cn2)");
		identifiers.put(cn3, "re.Edge1(cn1.Edge2(cn3))");
		identifiers.put(cn4, "re.Edge1(cn1.Edge3(cn4))");
		identifiers.put(cn5, "re.Edge4(cn2.Edge5(cn5))");
		identifiers.put(cn6, "re.Edge4(cn2.Edge6(cn6))");

		for (Entry<IStructureElement, String> entry : identifiers.entrySet()) {
			assertEquals(String.format("Cannot find path %s in graph!", entry.getValue()),
					entry.getKey(),
					structureGraph.getStructureElement(entry.getValue()));
		}
	}

	@Test
	public void getIdentifer() {
		Map<String, IStructureElement> structureElements = new HashMap<>();
		structureElements.put("re", re);
		structureElements.put("re.Edge1(cn1)", cn1);
		structureElements.put("re.Edge4(cn2)", cn2);
		structureElements.put("re.Edge1(cn1.Edge2(cn3))", cn3);
		structureElements.put("re.Edge1(cn1.Edge3(cn4))", cn4);
		structureElements.put("re.Edge4(cn2.Edge5(cn5))", cn5);
		structureElements.put("re.Edge4(cn2.Edge6(cn6))", cn6);

		for (Entry<String, IStructureElement> entry : structureElements.entrySet()) {
			assertEquals(String.format("Cannot find element %s in graph!", entry.getValue()),
					entry.getKey(),
					structureGraph.getIdentifier(entry.getValue()));
		}
	}

	@Test
	public void getPath() {
		Map<IStructureElement, String> paths = new HashMap<>();
		paths.put(re, "");
		paths.put(cn1, "re.Edge1");
		paths.put(cn2, "re.Edge4");
		paths.put(cn3, "re.Edge1(cn1.Edge2)");
		paths.put(cn4, "re.Edge1(cn1.Edge3)");
		paths.put(cn5, "re.Edge4(cn2.Edge5)");
		paths.put(cn6, "re.Edge4(cn2.Edge6)");

		for (Entry<IStructureElement, String> entry : paths.entrySet()) {
			assertEquals(String.format("Cannot find path %s in graph!", entry.getValue()),
					entry.getValue(),
					structureGraph.getPath(entry.getKey()));
		}
	}

	@Test
	public void getIdentifiers() {
		List<String> identifiers = new ArrayList<>();
		identifiers.add("re");
		identifiers.add("re.Edge1(cn1)");
		identifiers.add("re.Edge4(cn2)");
		identifiers.add("re.Edge1(cn1.Edge2(cn3))");
		identifiers.add("re.Edge1(cn1.Edge3(cn4))");
		identifiers.add("re.Edge4(cn2.Edge5(cn5))");
		identifiers.add("re.Edge4(cn2.Edge6(cn6))");

		for (String identifier : identifiers) {
			assertTrue(String.format("Cannot find identifier %s in list!", identifier),
					structureGraph.getIdentifiers().contains(identifier));
		}
	}

	@Test
	public void getStructureElementsByPath() {
		Map<String, Integer> structureElements = new HashMap<>();
		structureElements.put("", 7);
		structureElements.put("re", 6);
		structureElements.put("re.Edge1", 3);
		structureElements.put("re.Edge4", 3);
		structureElements.put("re.Edge1(cn1.Edge2)", 1);
		structureElements.put("re.Edge1(cn1.Edge3)", 1);
		structureElements.put("re.Edge4(cn2.Edge5)", 1);
		structureElements.put("re.Edge4(cn2.Edge6)", 1);

		for (Entry<String, Integer> entry : structureElements.entrySet()) {
			assertEquals(String.format("Cannot find element %s in graph!", entry.getKey()),
					entry.getValue().intValue(),
					structureGraph.getStructureElements(entry.getKey()).size());
		}
	}

	@Test
	public void getStructureElementsByPathAndAncestorsOnly() {
		Map<String, Integer> structureElements = new HashMap<>();
		structureElements.put("", 1);
		structureElements.put("re", 2);
		structureElements.put("re.Edge1", 1);
		structureElements.put("re.Edge4", 1);
		structureElements.put("re.Edge1(cn1.Edge2)", 1);
		structureElements.put("re.Edge1(cn1.Edge3)", 1);
		structureElements.put("re.Edge4(cn2.Edge5)", 1);
		structureElements.put("re.Edge4(cn2.Edge6)", 1);

		for (Entry<String, Integer> entry : structureElements.entrySet()) {
			assertEquals(String.format("Cannot find element %s in graph!", entry.getKey()),
					entry.getValue().intValue(),
					structureGraph.getStructureElements(entry.getKey(), true).size());
		}
	}
}
