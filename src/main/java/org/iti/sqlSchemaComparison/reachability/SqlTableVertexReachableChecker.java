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

package org.iti.sqlSchemaComparison.reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class SqlTableVertexReachableChecker implements
		ISqlElementReachabilityChecker {

	private DirectedGraph<IStructureElement, DefaultEdge> schema;
	private SqlTableVertex table;

	private boolean reachable = false;

	@Override
	public boolean isReachable() {
		return reachable;
	}

	private List<ISqlElement> path = new ArrayList<>();

	@Override
	public List<ISqlElement> getPath() {
		return path;
	}

	public SqlTableVertexReachableChecker(DirectedGraph<IStructureElement, DefaultEdge> schema, ISqlElement table) {
		this.schema = schema;
		this.table = (SqlTableVertex) table;

		CheckReachability();
	}

	private void CheckReachability() {
		Set<ISqlElement> tables = SqlElementFactory.getSqlElementsOfType(SqlTableVertex.class, schema.vertexSet());

		if (tables.contains(table))
		{
			reachable = true;
			path.add(table);
		}
	}

}
