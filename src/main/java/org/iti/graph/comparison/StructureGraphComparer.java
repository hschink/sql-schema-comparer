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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.iti.graph.IStructureGraph;
import org.iti.graph.nodes.IStructureElement;

public class StructureGraphComparer implements IStructureGraphComparer {

	public static class AmbiguousRenameException extends StructureGraphComparisonException {
		private static final long serialVersionUID = -3176377321899125075L;
	}

	public static class AmbiguousMoveException extends StructureGraphComparisonException {
		private static final long serialVersionUID = -3666070878704536627L;
	}

	private StructureGraphComparisonResult result;

	private IStructureGraph oldGraph;
	private IStructureGraph newGraph;

	private Map<String, List<IStructureElement>> removedNodesByPath;
	private Map<String, List<IStructureElement>> addedNodesByPath;

	@Override
	public StructureGraphComparisonResult compare(IStructureGraph oldGraph,
			IStructureGraph newGraph) throws StructureGraphComparisonException {
		SimpleStructureGraphComparer simpleStructureGraphComparer = new SimpleStructureGraphComparer();

		result = simpleStructureGraphComparer.compare(oldGraph, newGraph);

		this.oldGraph = oldGraph;
		this.newGraph = newGraph;

		groupAddedAndRemovedNodesByPath();

		setRenamedNodes();
		setMovedNodes();

		return result;
	}

	private void groupAddedAndRemovedNodesByPath() {
		Collection<IStructureElement> removedNodes = result.getElementsByModification(StructureElementModification.Type.NodeDeleted);
		Collection<IStructureElement> addedNodes = result.getElementsByModification(StructureElementModification.Type.NodeAdded);

		removedNodesByPath = getNodesByPath(oldGraph, removedNodes);
		addedNodesByPath = getNodesByPath(newGraph, addedNodes);
	}

	private static Map<String, List<IStructureElement>> getNodesByPath(
			IStructureGraph graph,
			Collection<IStructureElement> elements) {
		Map<String, List<IStructureElement>> missingNodesByPath = new HashMap<>();

		for (IStructureElement element : elements) {
			String path = graph.getPath(element);

			if (!missingNodesByPath.containsKey(path)) {
				missingNodesByPath.put(path, new ArrayList<IStructureElement>());
			}

			missingNodesByPath.get(path).add(element);
		}

		return missingNodesByPath;
	}

	private void setRenamedNodes() throws AmbiguousRenameException {
		for (Entry<String, List<IStructureElement>> removedInPath : removedNodesByPath.entrySet()) {
			for (IStructureElement removedElement : removedInPath.getValue()) {
				IStructureElement renamedElement = findRenamedElement(removedInPath.getKey(), removedElement);

				exchangeNode(removedElement, renamedElement, StructureElementModification.Type.NodeRenamed);
			}
		}
	}

	private IStructureElement findRenamedElement(String path,
			IStructureElement removedElement) throws AmbiguousRenameException {
		List<IStructureElement> addedElementsInPath = addedNodesByPath.get(path);

		if (addedElementsInPath != null) {
			switch (addedElementsInPath.size()) {
				case 0: return null;
				case 1:
					IStructureElement addedElement = addedElementsInPath.get(0);

					return addedElement;
				default: throw new AmbiguousRenameException();
			}
		}

		return null;
	}

	private void exchangeNode(IStructureElement removedElement,
			IStructureElement addedElement,
			StructureElementModification.Type type) {
		if (addedElement != null) {
			String identifier = result.getNewGraph().getIdentifier(addedElement);
			String path = result.getNewGraph().getPath(addedElement);
			StructureElementModification modification = new StructureElementModification(path, addedElement.getIdentifier(), type);

			result.removeModification(oldGraph.getIdentifier(removedElement));
			result.removeModification(newGraph.getIdentifier(addedElement));

			result.addModification(identifier, modification);
		}
	}

	private void setMovedNodes() throws AmbiguousMoveException {
		for (Entry<String, List<IStructureElement>> removedInPath : removedNodesByPath.entrySet()) {
			for (IStructureElement removedElement : removedInPath.getValue()) {
				IStructureElement movedElement = findMovedElement(removedElement);
				
				exchangeNode(removedElement, movedElement, StructureElementModification.Type.NodeMoved);
			}
		}
	}

	private IStructureElement findMovedElement(IStructureElement element) throws AmbiguousMoveException {
		Collection<IStructureElement> addedElements;
		
		addedElements = result.getElementsByIdentifier(element.getIdentifier(), StructureElementModification.Type.NodeAdded);

		switch (addedElements.size()) {
			case 0: return null;
		
			case 1: 
				IStructureElement addedElement = addedElements.iterator().next();

				return addedElement;

			default: throw new AmbiguousMoveException();
		}
	}
}