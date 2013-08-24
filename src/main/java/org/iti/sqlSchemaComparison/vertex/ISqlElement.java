package org.iti.sqlSchemaComparison.vertex;

public interface ISqlElement {

	SqlElementType getSqlElementType();
	String getSqlElementId();
	
	Object getSourceElement();
	void setSourceElement(Object sourceElement);
}
