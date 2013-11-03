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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iti.graph.IStructureGraph;
import org.iti.graph.nodes.IStructureElement;

public class SimpleStructureGraphComparer implements IStructureGraphComparer {

	public StructureGraphComparisonResult compare(IStructureGraph oldGraph,
			IStructureGraph newGraph) {
		StructureGraphComparisonResult result = new StructureGraphComparisonResult();
		List<String> removedNodeIds = getMissingNodeIds(oldGraph, newGraph);
		List<String> addedNodeIds = getMissingNodeIds(newGraph, oldGraph);
//		Map<String, List<IStructureElement>> removedNodesByPath = getNodesByPath(oldGraph, removedNodeIds);
//		Map<String, List<IStructureElement>> addedNodesByPath = getNodesByPath(newGraph, addedNodeIds);

		addNodesWithModificationToResult(getNodes(oldGraph, removedNodeIds),
				StructureElementModification.NodeDeleted,
				result);
		addNodesWithModificationToResult(getNodes(newGraph, addedNodeIds),
				StructureElementModification.NodeAdded,
				result);

		return result;
	}

	private static List<String> getMissingNodeIds(IStructureGraph oldGraph,
			IStructureGraph newGraph) {
		List<String> oldNodes = new ArrayList<>(oldGraph.getIdentifiers());
		List<String> newNodes = new ArrayList<>(newGraph.getIdentifiers());

		oldNodes.removeAll(newNodes);

		return oldNodes;
	}	

	private static List<IStructureElement> getNodes(IStructureGraph graph,
			List<String> nodeIds) {
		List<IStructureElement> nodes = new ArrayList<>();

		for (String identifier : nodeIds) {
			nodes.add(graph.getStructureElement(identifier));
		}

		return nodes;
	}

	private void addNodesWithModificationToResult(
			List<IStructureElement> elements,
			StructureElementModification modification,
			StructureGraphComparisonResult result) {
		for (IStructureElement element : elements) {
			result.addModification(element, modification);
		}
	}

	private static Map<String, List<IStructureElement>> getNodesByPath(
			IStructureGraph graph,
			List<String> nodeIds) {
		Map<String, List<IStructureElement>> missingNodesByPath = new HashMap<>();

		for (String identifier : nodeIds) {
			IStructureElement element = graph.getStructureElement(identifier);
			String path = graph.getPath(element);

			if (!missingNodesByPath.containsKey(path)) {
				missingNodesByPath.put(path, new ArrayList<IStructureElement>());
			}

			missingNodesByPath.get(path).add(element);
		}

		return missingNodesByPath;
	}
}
