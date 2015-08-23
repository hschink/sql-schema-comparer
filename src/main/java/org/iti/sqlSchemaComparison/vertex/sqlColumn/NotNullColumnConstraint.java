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

	public NotNullColumnConstraint(String expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return ColumnConstraintHelper.toString(this);
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
