package org.iti.sqlSchemaComparison.vertex.sqlColumn;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;

public interface IColumnConstraint {

	enum ConstraintType {
		PRIMARY_KEY,
		NOT_NULL,
		UNIQUE,
		CHECK,
		DEFAULT,
		COLLATE,
		FOREIGN_KEY
	}
	
	ConstraintType getConstraintType();
	
	String getConstraintExpression();
	
	ISqlElement getColumnVertex();
}
