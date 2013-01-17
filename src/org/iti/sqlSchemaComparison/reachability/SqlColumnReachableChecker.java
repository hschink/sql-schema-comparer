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
