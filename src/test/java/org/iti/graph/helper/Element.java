package org.iti.graph.helper;

import org.iti.graph.nodes.IStructureElement;

public class Element implements IStructureElement {

	private String identifier = "";
	
	public Element(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}
}
