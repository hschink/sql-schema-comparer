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

public class StructureGraphComparisonResult {

	private IStructureGraph oldGraph;

	public IStructureGraph getOldGraph() {
		return oldGraph;
	}

	private IStructureGraph newGraph;

	public IStructureGraph getNewGraph() {
		return newGraph;
	}

	private Map<String, StructureElementModification> modifications = new HashMap<>();

	public Map<String, StructureElementModification> getModifications() {
		return modifications;
	}

	public void addModification(String path, StructureElementModification modification) {
		modifications.put(path, modification);
	}

	public void removeModification(String fullIdentifier) {
		modifications.remove(fullIdentifier);
	}

	public StructureGraphComparisonResult(IStructureGraph oldGraph,
			IStructureGraph newGraph) {
		this.oldGraph = oldGraph;
		this.newGraph = newGraph;
	}

	public Collection<IStructureElement> getElementsByModification(StructureElementModification.Type type) {
		List<IStructureElement> elements = new ArrayList<>();

		for (Entry<String, StructureElementModification> m : modifications.entrySet()) {
			if (m.getValue().getType() == type) {
				elements.add(getElementByPath(m.getKey()));
			}
		}

		return elements;
	}

	private IStructureElement getElementByPath(String path) {
		if (oldGraph.containsElementWithPath(path)) {
			return oldGraph.getStructureElement(path);
		} else if (newGraph.containsElementWithPath(path)) {
			return newGraph.getStructureElement(path);
		}

		return null;
	}

	public Collection<IStructureElement> getElementsByIdentifier(String identifier,
			StructureElementModification.Type type) {
		List<IStructureElement> elements = new ArrayList<>();

		for (Entry<String, StructureElementModification> m : modifications.entrySet()) {
			IStructureElement element = getElementByPath(m.getKey());

			if (m.getValue().getType() == type && element.getIdentifier().equals(identifier)) {
				elements.add(getElementByPath(m.getKey()));
			}
		}

		return elements;
	}
}