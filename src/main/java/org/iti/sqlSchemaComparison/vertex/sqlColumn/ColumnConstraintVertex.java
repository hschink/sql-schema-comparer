package org.iti.sqlSchemaComparison.vertex.sqlColumn;

public class ColumnConstraintVertex implements IColumnConstraint {

	private static final long serialVersionUID = -7800801666223788199L;

	private String columnName;
	private ConstraintType constraintType;
	private String expression = "";

	public ColumnConstraintVertex(String columnName, ConstraintType constraintType) {
		this.columnName = columnName;
		this.constraintType = constraintType;
	}

	public ColumnConstraintVertex(String columnName, ConstraintType constraintType, String expression) {
		this(columnName, constraintType);

		this.expression = expression;
	}

	@Override
	public ConstraintType getConstraintType() {
		return constraintType;
	}

	@Override
	public String getConstraintExpression() {
		return expression;
	}

	@Override
	public String getName() {
		StringBuilder builder = new StringBuilder();

		builder.append("[Constraint]");
		builder.append(" ");
		builder.append(columnName);
		builder.append(".");
		builder.append(constraintType.toString());

		if (!expression.equals("")) {
			builder.append("(");
			builder.append(expression);
			builder.append(")");
		}

		return builder.toString();
	}

	@Override
	public boolean isMandatory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOptionalList() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getSourceElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void setSourceElement(Object sourceElement) {
		// TODO Auto-generated method stub

	}

}
