package org.iti.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class StructureManager implements IStructureManager {

	private DirectedGraph<IStructureElement, DefaultEdge> graph;
	
	private Map<String, IStructureElement> elementsByIdentifer = new HashMap<>();
	
	public StructureManager(DirectedGraph<IStructureElement, DefaultEdge> graph) {
		this.graph = graph;
		
		loadElementsByIdentifier();
	}

	private void loadElementsByIdentifier() {
		for (IStructureElement element : graph.vertexSet()) {
			String identifier = getIdentifier(element);
			elementsByIdentifer.put(identifier, element);
		}
	}

	private String getIdentifier(IStructureElement element) {
		List<IStructureElement> pathElements = getElementPathElements(element);

		return getPathString(pathElements);
	}

	private List<IStructureElement> getElementPathElements(IStructureElement element) {
		IStructureElement currentElement = element; 
		LinkedList<IStructureElement> elements = new LinkedList<>();

		while (currentElement != null) {
			elements.addFirst(currentElement);

			DefaultEdge incomingEdge = getIncomingEdge(graph.incomingEdgesOf(currentElement));
			currentElement = getParent(incomingEdge);
		}

		return elements;
	}

	private DefaultEdge getIncomingEdge(Set<DefaultEdge> incomingEdgesOf) {
		if (incomingEdgesOf != null && incomingEdgesOf.size() > 0) {
			return incomingEdgesOf.iterator().next();
		}
		
		return null;
	}

	private IStructureElement getParent(DefaultEdge incomingEdge) {
		if (incomingEdge != null) {
			for (IStructureElement element : graph.vertexSet()) {
				if (graph.outgoingEdgesOf(element).contains(incomingEdge)) {
					return element;
				}
			}
		}
		
		return null;
	}

	private String getPathString(List<IStructureElement> pathElements) {
		StringBuilder identifier = new StringBuilder();

		for (int x = 0; x < pathElements.size(); x++) {
			boolean successorExists = x + 1 < pathElements.size();
			IStructureElement element1 = pathElements.get(x);
			IStructureElement element2 = (successorExists) ? pathElements.get(x + 1) : null;
			DefaultEdge edge = (successorExists) ? getEdge(element1, element2) : null;

			identifier.append(element1.getIdentifier());

			if (successorExists) {
				identifier.append("." + edge.getClass().getSimpleName() + "(");
			}
		}

		identifier.append(StringUtils.repeat(")", pathElements.size() - 1));

		return identifier.toString();
	}

	private DefaultEdge getEdge(IStructureElement element1,
			IStructureElement element2) {
		return graph.getAllEdges(element1, element2).iterator().next();
	}

	@Override
	public IStructureElement getStructureElement(String identifier) {
		return elementsByIdentifer.get(identifier);
	}
}
