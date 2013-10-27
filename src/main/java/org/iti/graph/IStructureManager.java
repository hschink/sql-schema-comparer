package org.iti.graph;

import org.iti.graph.nodes.IStructureElement;

public interface IStructureManager {

	IStructureElement getStructureElement(String identifier);

	String getIdentifier(IStructureElement structureElement);

	String getPath(IStructureElement structureElement);
}
