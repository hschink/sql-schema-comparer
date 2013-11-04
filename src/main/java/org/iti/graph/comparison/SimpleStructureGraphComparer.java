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
import java.util.List;

import org.iti.graph.IStructureGraph;
import org.iti.graph.nodes.IStructureElement;

public class SimpleStructureGraphComparer implements IStructureGraphComparer {

	public StructureGraphComparisonResult compare(IStructureGraph oldGraph,
			IStructureGraph newGraph) {
		StructureGraphComparisonResult result = new StructureGraphComparisonResult(oldGraph, newGraph);
		List<String> removedNodeIds = getMissingNodeIds(oldGraph, newGraph);
		List<String> addedNodeIds = getMissingNodeIds(newGraph, oldGraph);

		addNodesWithModificationToResult(oldGraph,
				removedNodeIds,
				StructureElementModification.NodeDeleted,
				result);
		addNodesWithModificationToResult(
				newGraph,
				addedNodeIds,
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

	private static void addNodesWithModificationToResult(IStructureGraph graph,
			List<String> nodeIds,
			StructureElementModification modification,
			StructureGraphComparisonResult result) {
		List<IStructureElement> elements = getNodes(graph, nodeIds);

		for (IStructureElement element : elements) {
			result.addModification(graph.getIdentifier(element), modification);
		}
	}

	private static List<IStructureElement> getNodes(IStructureGraph graph,
			List<String> nodeIds) {
		List<IStructureElement> nodes = new ArrayList<>();

		for (String identifier : nodeIds) {
			nodes.add(graph.getStructureElement(identifier));
		}

		return nodes;
	}
}
