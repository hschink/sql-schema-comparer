package org.iti.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
		return getPathString(pathElements, true);
	}

	private String getPathString(List<IStructureElement> pathElements,
			boolean includeLastElement) {
		StringBuilder identifier = new StringBuilder();
		int closingBracketCount = pathElements.size() - 1 - ((includeLastElement) ? 0 : 1);

		for (int x = 0; x < pathElements.size(); x++) {
			boolean isLastElement = x == pathElements.size() - 1;
			boolean isNextToLastElement = x == pathElements.size() - 2;

			if (!isLastElement || includeLastElement) {
				boolean successorExists = x + 1 < pathElements.size();
				IStructureElement element1 = pathElements.get(x);
				IStructureElement element2 = (successorExists) ? pathElements.get(x + 1) : null;
				DefaultEdge edge = (successorExists) ? getEdge(element1, element2) : null;

				identifier.append(element1.getIdentifier());

				if (successorExists) {
					identifier.append("." + edge.getClass().getSimpleName());

					if (!isNextToLastElement || includeLastElement) {
						identifier.append("(");
					}
				}
			}
		}

		identifier.append(StringUtils.repeat(")", closingBracketCount));

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

	@Override
	public String getIdentifier(IStructureElement element) {
		List<IStructureElement> pathElements = getElementPathElements(element);

		return getPathString(pathElements);
	}

	@Override
	public String getPath(IStructureElement element) {
		List<IStructureElement> pathElements = getElementPathElements(element);

		return getPathString(pathElements, false);
	}

	@Override
	public List<IStructureElement> getStructureElements(String path) {
		return getStructureElements(path, false);
	}

	@Override
	public List<IStructureElement> getStructureElements(String path,
			boolean directAncestorsOnly) {
		Map<IStructureElement, Integer> list = new HashMap<>();

		for (IStructureElement element : elementsByIdentifer.values()) {
			if (getPath(element).startsWith(path)) {
				list.put(element, getElementPathElements(element).size());
			}
		}

		if (list.size() > 0 && directAncestorsOnly) {
			removeIndirectAncestors(list);
		}

		return new ArrayList<IStructureElement>(list.keySet());
	}

	private void removeIndirectAncestors(Map<IStructureElement, Integer> list) {
		List<IStructureElement> elementsToRemove = new ArrayList<>();
		int minimalElementsCount = Integer.MAX_VALUE;

		for (Integer i : list.values()) {
			if (minimalElementsCount > i) {
				minimalElementsCount = i;
			}
		}

		for (Entry<IStructureElement, Integer> entry : list.entrySet()) {
			if (entry.getValue() > minimalElementsCount) {
				elementsToRemove.add(entry.getKey());
			}
		}

		for (IStructureElement elementToRemove : elementsToRemove) {
			list.remove(elementToRemove);
		}
	}
}