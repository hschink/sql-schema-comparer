/*
 *  Copyright 1999 Hagen Schink <hagen.schink@gmail.com>
 *
 *  This file is part of sql-schema-comparer.
 *
 *  sql-schema-comparer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  sql-schema-comparer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with sql-schema-comparer.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.iti.sqlSchemaComparison;

import java.util.List;

import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;

public class SqlSchemaColumnComparisonResult {

	public static class ColumnConstraintComparisonResult {
		
		private List<IColumnConstraint> addedConstraints;

		public List<IColumnConstraint> getAddedConstraints() {
			return addedConstraints;
		}
		
		private List<IColumnConstraint> removedConstraints;

		public List<IColumnConstraint> getRemovedConstraints() {
			return removedConstraints;
		}
		
		public ColumnConstraintComparisonResult(List<IColumnConstraint> addedConstraints,
				List<IColumnConstraint> removedConstraints) {
			
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
