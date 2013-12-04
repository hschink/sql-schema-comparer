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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class StructureGraph implements IStructureGraph {

	private DirectedGraph<IStructureElement, DefaultEdge> graph;
	
	private Map<String, IStructureElement> elementsByIdentifer = new HashMap<>();
	private Map<String, DefaultEdge> pathesByIdentifer = new HashMap<>();
	
	public StructureGraph(DirectedGraph<IStructureElement, DefaultEdge> graph) {
		this.graph = graph;
		
		loadElementsByIdentifier();
		loadEdgesByIdentifier();
	}

	private void loadElementsByIdentifier() {
		for (IStructureElement element : graph.vertexSet()) {
			String identifier = getIdentifier(element);
			elementsByIdentifer.put(identifier, element);
		}
	}

	private void loadEdgesByIdentifier() {
		for (IStructureElement element : elementsByIdentifer.values()) {
			List<DefaultEdge> incomingEdges = getIncomingEdges(element);

			for (DefaultEdge incomingEdge : incomingEdges) {
				for (IStructureElement relatedElement : graph.vertexSet()) {
					if (graph.outgoingEdgesOf(relatedElement).contains(incomingEdge)) {
						List<IStructureElement> pathElements = new ArrayList<>();

						pathElements.add(relatedElement);
						pathElements.add(element);

						pathesByIdentifer.put(getPathString(pathElements, false), incomingEdge);
					}
				}
			}
		}
	}

	private List<IStructureElement> getElementPathElements(IStructureElement element) {
		return getElementPathElements(element, true);
	}

	private List<IStructureElement> getElementPathElements(IStructureElement element, boolean toRootElement) {
		IStructureElement currentElement = element; 
		LinkedList<IStructureElement> elements = new LinkedList<>();
		int elementCount = (toRootElement) ? -1 : 2;

		while (currentElement != null && elementCount != 0) {
			elements.addFirst(currentElement);

			currentElement = getParent(currentElement);

			elementCount--;
		}

		return elements;
	}

	private IStructureElement getParent(IStructureElement element) {
		List<DefaultEdge> incomingEdges = getIncomingEdges(element);

		if (!incomingEdges.isEmpty()) {
			DefaultEdge incomingEdge = incomingEdges.get(0);

			for (IStructureElement e : graph.vertexSet()) {
				if (graph.outgoingEdgesOf(e).contains(incomingEdge)) {
					return e;
				}
			}
		}
		
		return null;
	}

	private List<DefaultEdge> getIncomingEdges(IStructureElement element) {
		return new ArrayList<DefaultEdge>(graph.incomingEdgesOf(element));
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
	public boolean containsElementWithPath(String path) {
		return elementsByIdentifer.containsKey(path);
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
	public DefaultEdge getEdge(String path) {
		return pathesByIdentifer.get(path);
	}

	@Override
	public IStructureElement getSourceElement(DefaultEdge edge) {
		return graph.getEdgeSource(edge);
	}

	@Override
	public IStructureElement getTargetElement(DefaultEdge edge) {
		return graph.getEdgeTarget(edge);
	}

	@Override
	public String getPath(IStructureElement element, boolean toRootElement) {
		List<IStructureElement> pathElements = getElementPathElements(element, toRootElement);

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

	@Override
	public List<String> getIdentifiers() {
		return new ArrayList<String>(elementsByIdentifer.keySet());
	}

	@Override
	public List<String> getPathes() {
		return new ArrayList<String>(pathesByIdentifer.keySet());
	}

}