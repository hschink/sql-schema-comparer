package org.iti.graph.comparison;

import java.util.HashMap;
import java.util.Map;

import org.iti.graph.nodes.IStructureElement;

public class StructureGraphComparisonResult {

	private Map<IStructureElement, StructureElementModification> modifications = new HashMap<>();

	public Map<IStructureElement, StructureElementModification> getModifications() {
		return modifications;
	}

	public void addModification(IStructureElement element,
			StructureElementModification modification) {
		modifications.put(element, modification);
	}
}