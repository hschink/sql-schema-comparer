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

package org.iti.sqlSchemaComparison.edge;

import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.graph.DefaultEdge;

public class TableHasColumnEdge extends DefaultEdge implements ITableHasColumnEdge {

	private static final long serialVersionUID = 1L;

	private SqlTableVertex table;

	@Override
	public SqlTableVertex getTable() {
		return table;
	}

	private SqlColumnVertex column;

	@Override
	public SqlColumnVertex getColumn() {
		return column;
	}

	public TableHasColumnEdge(IStructureElement table, IStructureElement column) {
		this.table = (SqlTableVertex) table;
		this.column = (SqlColumnVertex) column;
	}

	@Override
	public String toString() {
		return table.getName() + " (" + column.getName() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ITableHasColumnEdge) {
			ITableHasColumnEdge e = (ITableHasColumnEdge)o;

			return table.equals(e.getTable()) && column.equals(e.getColumn());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return table.hashCode() + column.hashCode();
	}

}
