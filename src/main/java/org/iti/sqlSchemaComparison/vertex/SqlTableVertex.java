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

package org.iti.sqlSchemaComparison.vertex;


public class SqlTableVertex implements ISqlElement {

	private static final long serialVersionUID = 5594278073539735449L;

	private Object sourceElement = null;
	
	@Override
	public SqlElementType getSqlElementType() {
		return SqlElementType.Table;
	}
	
	private String id = "";

	@Override
	public String getSqlElementId() {
		// TODO Auto-generated method stub
		return id;
	}

	public SqlTableVertex(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "[" + getSqlElementType() + "] " + getSqlElementId();
	}

	@Override
	public boolean equals(Object o) {
		return SqlElementFactory.equals(this, o);
	}
	
	@Override
	public int hashCode() {
		return SqlElementFactory.hashCode(this);
	}

	@Override
	public Object getSourceElement() {
		return sourceElement;
	}

	@Override
	public void setSourceElement(Object sourceElement) {
		this.sourceElement = sourceElement;
	}

	@Override
	public String getIdentifier() {
		return getSqlElementId();
	}
}
