package org.iti.sqlSchemaComparison.vertex.sqlColumn;


public class ColumnConstraintHelper {

	public static boolean equals(IColumnConstraint columnConstraint, Object o) {

		if (o instanceof IColumnConstraint) {
			IColumnConstraint constraint = (IColumnConstraint) o;
			
			return columnConstraint.getConstraintType() == constraint.getConstraintType()
					&& columnConstraint.getConstraintExpression().equals(constraint.getConstraintExpression());
		}
		
		return false;
	}

	public static int hashCode(IColumnConstraint columnConstraint) {
		return columnConstraint.getConstraintType().hashCode()
				+ columnConstraint.getConstraintExpression().hashCode();
	}

}
