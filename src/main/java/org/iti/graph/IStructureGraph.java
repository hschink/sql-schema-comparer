package org.iti.graph;

import java.util.List;

import org.iti.graph.nodes.IStructureElement;

public interface IStructureGraph {

	IStructureElement getStructureElement(String identifier);

	String getIdentifier(IStructureElement structureElement);

	String getPath(IStructureElement structureElement);

	List<String> getIdentifiers();

	List<IStructureElement> getStructureElements(String path);

	List<IStructureElement> getStructureElements(String path, boolean directAncestorsOnly);
}
