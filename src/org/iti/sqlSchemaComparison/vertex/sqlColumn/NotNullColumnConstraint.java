package org.iti.sqlSchemaComparison.vertex.sqlColumn;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;

public class NotNullColumnConstraint implements IColumnConstraint {

	@Override
	public ConstraintType getConstraintType() {
		return ConstraintType.NOT_NULL;
	}
	
	private String expression;

	@Override
	public String getConstraintExpression() {
		return expression;
	}
	
	private ISqlElement vertex;

	@Override
	public ISqlElement getColumnVertex() {
		return vertex;
	}
	
	public NotNullColumnConstraint(String expression, ISqlElement vertex) {
		this.expression = expression;
		this.vertex = vertex;
	}

	@Override
	public boolean equals(Object o) {
		return ColumnConstraintHelper.equals(this, o);
	}
	
	@Override
	public int hashCode() {
		return ColumnConstraintHelper.hashCode(this);
	}

}
