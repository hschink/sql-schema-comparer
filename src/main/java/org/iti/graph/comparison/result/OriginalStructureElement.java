package org.iti.graph.comparison.result;


public class OriginalStructureElement implements IModificationDetail {

	private String path;

	@Override
	public String getPath() {
		return path;
	}

	private String identifier;

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public OriginalStructureElement(String path, String identifier) {
		this.path = path;
		this.identifier = identifier;
	}
}
