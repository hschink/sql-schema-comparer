package org.iti.graph;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StructureManagerTest {

	private static class Element implements IStructureElement {
		private String identifier = "";
		
		public Element(String identifier) {
			this.identifier = identifier;
		}

		@Override
		public String getIdentifier() {
			return identifier;
		}
	}
	
	private static class Edge1 extends DefaultEdge {
		private static final long serialVersionUID = -3077628654425380054L;
	}
	private static class Edge2 extends DefaultEdge {
		private static final long serialVersionUID = -1379475009596156583L;
	}
	private static class Edge3 extends DefaultEdge {

		private static final long serialVersionUID = 5858860053015006782L;
	}
	private static class Edge4 extends DefaultEdge {

		private static final long serialVersionUID = 6290153527153548731L;
	}
	private static class Edge5 extends DefaultEdge {
		private static final long serialVersionUID = -3583903991701633094L;
	}
	private static class Edge6 extends DefaultEdge {
		private static final long serialVersionUID = 1644621201081668624L;
	}
	
	private static StructureManager manager;
	
	private static Element re = new Element("re");
	private static Element cn1 = new Element("cn1");
	private static Element cn2 = new Element("cn2");
	private static Element cn3 = new Element("cn3");
	private static Element cn4 = new Element("cn4");
	private static Element cn5 = new Element("cn5");
	private static Element cn6 = new Element("cn6");
	
	private static DirectedGraph<IStructureElement, DefaultEdge> graph1;
	private static DirectedGraph<IStructureElement, DefaultEdge> graph2;
	
	@BeforeClass
	public static void init() throws Exception {
		graph1 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
		graph2 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
		
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
		
		graph2.addVertex(re);
		graph2.addVertex(cn1);
		graph2.addVertex(cn2);
		graph2.addVertex(cn3);
		graph2.addVertex(cn4);
		graph2.addVertex(cn5);
		graph2.addVertex(cn6);
		
		graph2.addEdge(re, cn1, new Edge1());
		graph2.addEdge(cn1, cn3, new Edge2());
		graph2.addEdge(cn1, cn4, new Edge3());
		graph2.addEdge(re, cn2, new Edge4());
		graph2.addEdge(cn2, cn5, new Edge5());
		graph2.addEdge(cn2, cn6, new Edge6());
		
		manager = new StructureManager(graph1);
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
					manager.getStructureElement(entry.getValue()));
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
					manager.getIdentifier(entry.getValue()));
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
					manager.getPath(entry.getKey()));
		}
	}
}
