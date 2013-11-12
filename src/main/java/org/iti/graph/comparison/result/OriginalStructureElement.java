package org.iti.graph.comparison.result;


public class OriginalStructureElement implements IModificationDetail {

	private String identifier;

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public OriginalStructureElement(String identifier) {
		this.identifier = identifier;
	}
}
