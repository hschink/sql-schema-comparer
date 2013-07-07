package org.iti.sqlSchemaComparison;

import java.util.List;

import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;


public class SqlSchemaColumnComparisonResult {

	public static class ColumnConstraintComparisonResult {
		
		private List<IColumnConstraint> constraints1;
		
		public List<IColumnConstraint> getConstraints1() {
			return constraints1;
		}

		private List<IColumnConstraint> constraints2;

		public List<IColumnConstraint> getConstraints2() {
			return constraints2;
		}
		
		private List<IColumnConstraint> addedConstraints;

		public List<IColumnConstraint> getAddedConstraints() {
			return addedConstraints;
		}
		
		private List<IColumnConstraint> removedConstraints;

		public List<IColumnConstraint> getRemovedConstraints() {
			return removedConstraints;
		}
		
		public ColumnConstraintComparisonResult(List<IColumnConstraint> constraints1,
				List<IColumnConstraint> constraints2,
				List<IColumnConstraint> addedConstraints,
				List<IColumnConstraint> removedConstraints) {
			
			this.constraints1 = constraints1;
			this.constraints2 = constraints2;
			this.addedConstraints = addedConstraints;
			this.removedConstraints = removedConstraints;
		}
		
		@Override
		public String toString() {
			String result = "";
			
			if (addedConstraints.size() > 0) {
				result += "\n";
				result += "| CREATED CONSTRAINTS |\n";
				for (IColumnConstraint c : addedConstraints)
					result += String.format("%s\n", c);
			}
			
			if (removedConstraints.size() > 0) {
				result += "\n";
				result += "| REMOVED CONSTRAINTS |\n";
				for (IColumnConstraint c : removedConstraints)
					result += String.format("%s\n", c);
			}
			
			return result;
		}
	}
	
	private String oldColumnType;
	
	public String getOldColumnType() {
		return oldColumnType;
	}

	private String currentColumnType;

	public String getCurrentColumnType() {
		return currentColumnType;
	}
	
	public boolean hasColumnTypeChanged() {
		return !oldColumnType.equals(currentColumnType);
	}
	
	private ColumnConstraintComparisonResult constraintComparisonResult;
	
	public ColumnConstraintComparisonResult getConstraintComparisonResult() {
		return constraintComparisonResult;
	}

	public SqlSchemaColumnComparisonResult(String oldColumnType,
			String currentColumnType,
			ColumnConstraintComparisonResult constraintComparisonResult) {
		
		this.oldColumnType = oldColumnType;
		this.currentColumnType = currentColumnType;
		this.constraintComparisonResult = constraintComparisonResult;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		if (hasColumnTypeChanged())
			result += String.format("Type: %s -> %s\n", oldColumnType, currentColumnType);
		
		result += constraintComparisonResult.toString();
		
		return result;
	}
}
