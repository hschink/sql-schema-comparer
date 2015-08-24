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

package org.iti.sqlSchemaComparison.vertex.sqlColumn;

import org.iti.sqlSchemaComparison.SqlSchemaColumnComparisonResult;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;

public class ColumnConstraintHelper {

	public static SqlSchemaColumnComparisonResult compare(ISqlElement columnElement1, ISqlElement columnElement2) {
		SqlColumnVertex column1 = (SqlColumnVertex) columnElement1;
		SqlColumnVertex column2 = (SqlColumnVertex) columnElement2;

		return new SqlSchemaColumnComparisonResult(column1.getType(),
				column2.getType());
	}

	public static String toString(IColumnConstraint columnConstraint) {
		return String.format("[%s] %s", columnConstraint.getConstraintType(), columnConstraint.getConstraintExpression());
	}
	
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
