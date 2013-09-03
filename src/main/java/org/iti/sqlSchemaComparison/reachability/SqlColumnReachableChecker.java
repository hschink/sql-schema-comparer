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

import org.iti.sqlSchemaComparison.edge.IForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.edge.ITableHasColumnEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

public class SqlColumnReachableChecker implements
		ISqlElementReachabilityChecker {

	private Graph<ISqlElement, DefaultEdge> schema;
	private SqlTableVertex sourceTable;
	private SqlColumnVertex targetColumn;
	
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
	
	public SqlColumnReachableChecker(Graph<ISqlElement, DefaultEdge> schema, ISqlElement sourceTable, ISqlElement targetColumn) {
		this.schema = schema;
		this.sourceTable = (SqlTableVertex) sourceTable;
		this.targetColumn = (SqlColumnVertex) targetColumn;
		
		CheckReachability();
	}
	
	private void CheckReachability() {
		DijkstraShortestPath<ISqlElement, DefaultEdge> inspector = new DijkstraShortestPath<ISqlElement, DefaultEdge>(schema, sourceTable, targetColumn);
		
		reachable = inspector.getPath() != null;
		
		if (reachable) {
			for (DefaultEdge edge : inspector.getPath().getEdgeList()) {
				ISqlElement source = null;
				ISqlElement target = null;
				
				if (edge instanceof ITableHasColumnEdge) {
					source = ((ITableHasColumnEdge) edge).getTable();
					target = ((ITableHasColumnEdge) edge).getColumn();
				} else if (edge instanceof IForeignKeyRelationEdge) {
					source = ((IForeignKeyRelationEdge) edge).getReferencingColumn();
					target = ((IForeignKeyRelationEdge) edge).getForeignKeyColumn();
				}
				
				if (!path.contains(source))
					path.add(source);

				if (!path.contains(target))
					path.add(target);
			}
		}
	}

}
