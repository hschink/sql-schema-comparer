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
import org.iti.graph.comparison.result.StructureElementModification;
import org.iti.graph.comparison.result.StructureGraphComparisonResult;
import org.iti.graph.comparison.result.StructurePathModification;
import org.iti.graph.comparison.result.Type;
import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.graph.DefaultEdge;

public class SimpleStructureGraphComparer implements IStructureGraphComparer {

	public StructureGraphComparisonResult compare(IStructureGraph oldGraph,
			IStructureGraph newGraph) {
		StructureGraphComparisonResult result = new StructureGraphComparisonResult(oldGraph, newGraph);
		List<String> removedNodeIds = getMissingNodeIds(oldGraph, newGraph);
		List<String> addedNodeIds = getMissingNodeIds(newGraph, oldGraph);
		List<String> removedPathes = getMissingPathes(oldGraph, newGraph);
		List<String> addedPathes = getMissingPathes(newGraph, oldGraph);

		addNodesWithModificationToResult(oldGraph, removedNodeIds, Type.NodeDeleted, result);
		addNodesWithModificationToResult(newGraph, addedNodeIds, Type.NodeAdded, result);
		addPathesWithModificationToResult(oldGraph, removedPathes, Type.PathDeleted, result);
		addPathesWithModificationToResult(newGraph, addedPathes, Type.PathAdded, result);

		return result;
	}

	private static List<String> getMissingNodeIds(IStructureGraph oldGraph,
			IStructureGraph newGraph) {
		List<String> oldNodes = new ArrayList<>(oldGraph.getIdentifiers());
		List<String> newNodes = new ArrayList<>(newGraph.getIdentifiers());

		oldNodes.removeAll(newNodes);

		return oldNodes;
	}	

	private List<String> getMissingPathes(IStructureGraph oldGraph,
			IStructureGraph newGraph) {
		List<String> oldPathes = new ArrayList<>(oldGraph.getPathes());
		List<String> newPathes = new ArrayList<>(newGraph.getPathes());

		oldPathes.removeAll(newPathes);

		return oldPathes;
	}

	private static void addNodesWithModificationToResult(IStructureGraph graph,
			List<String> nodeIds,
			Type type,
			StructureGraphComparisonResult result) {
		List<IStructureElement> elements = getNodes(graph, nodeIds);

		for (IStructureElement element : elements) {
			String fullIdentifier = graph.getIdentifier(element);
			String path = graph.getPath(element);
			StructureElementModification modification = new StructureElementModification(path, element.getIdentifier(), type);

			result.addModification(fullIdentifier, modification);
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

	private void addPathesWithModificationToResult(IStructureGraph graph,
			List<String> pathes,
			Type type,
			StructureGraphComparisonResult result) {

		for (String path : pathes) {
			DefaultEdge edge = graph.getEdge(path);
			IStructureElement source = graph.getSourceElement(edge);
			IStructureElement target = graph.getSourceElement(edge);
			StructurePathModification modification = new StructurePathModification(path, edge, source, target, type);

			result.addModification(path, modification);
		}
	}
}
