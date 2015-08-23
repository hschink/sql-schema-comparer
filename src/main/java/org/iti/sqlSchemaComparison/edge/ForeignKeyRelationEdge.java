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

public class ForeignKeyRelationEdge extends DefaultEdge implements IForeignKeyRelationEdge {

	private static final long serialVersionUID = -1863489355018283003L;

	private SqlColumnVertex referencingColumn;

	@Override
	public SqlColumnVertex getReferencingColumn() {
		return referencingColumn;
	}

	private SqlTableVertex foreignKeyTable;

	@Override
	public SqlTableVertex getForeignKeyTable() {
		return foreignKeyTable;
	}

	private SqlColumnVertex foreignKeyColumn;

	@Override
	public SqlColumnVertex getForeignKeyColumn() {
		return foreignKeyColumn;
	}

	public ForeignKeyRelationEdge(IStructureElement referencingColumn,
			IStructureElement foreignKeyTable,
			IStructureElement foreignKeyColumn) {
		this.referencingColumn = (SqlColumnVertex) referencingColumn;
		this.foreignKeyTable = (SqlTableVertex) foreignKeyTable;
		this.foreignKeyColumn = (SqlColumnVertex) foreignKeyColumn;
	}

	@Override
	public String toString() {
		return referencingColumn.getName() + " --> "
				+ foreignKeyTable.getName()
				+ " (" + foreignKeyColumn.getName() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof IForeignKeyRelationEdge) {
			IForeignKeyRelationEdge e = (IForeignKeyRelationEdge)o;

			return referencingColumn.equals(e.getReferencingColumn())
					&& foreignKeyTable.equals(e.getForeignKeyTable())
					&& foreignKeyColumn.equals(e.getForeignKeyColumn());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return referencingColumn.hashCode() + foreignKeyTable.hashCode() + foreignKeyColumn.hashCode();
	}

}
