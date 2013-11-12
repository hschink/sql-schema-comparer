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

import java.util.ArrayList;
import java.util.List;

import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;

public class SqlColumnVertex implements ISqlElement {

	private static final long serialVersionUID = 2813536717531181481L;

	private Object sourceElement = null;
	
	@Override
	public SqlElementType getSqlElementType() {
		return SqlElementType.Column;
	}
	
	private String id = "";

	@Override
	public String getSqlElementId() {
		return id;
	}
	
	private String type = "";
	
	public String getType() {
		return type;
	}
	
	private String table = "";

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	private List<IColumnConstraint> constraints = new ArrayList<>();

	public List<IColumnConstraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<IColumnConstraint> constraints) {
		this.constraints = constraints;
	}

	public SqlColumnVertex(String id, String type, String table) {
		this.id = id;
		this.type = type;
		this.table = table;
	}
	
	@Override
	public String toString() {
		return "[" + getSqlElementType() + "] " + table + "." + getSqlElementId();
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
