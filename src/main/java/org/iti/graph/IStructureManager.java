package org.iti.graph;

import java.util.List;

import org.iti.graph.nodes.IStructureElement;

public interface IStructureManager {

	IStructureElement getStructureElement(String identifier);

	String getIdentifier(IStructureElement structureElement);

	String getPath(IStructureElement structureElement);

	List<IStructureElement> getStructureElements(String path);
}
