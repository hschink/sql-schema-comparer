package org.iti.sqlSchemaComparison.reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class SqlTableVertexReachableChecker implements
		ISqlElementReachabilityChecker {

	private Graph<ISqlElement, DefaultEdge> schema;
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

	public SqlTableVertexReachableChecker(Graph<ISqlElement, DefaultEdge> schema, ISqlElement table) {
		this.schema = schema;
		this.table = (SqlTableVertex) table;
		
		CheckReachability();
	}

	private void CheckReachability() {
		Set<ISqlElement> tables = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet());
		
		if (tables.contains(table))
		{
			reachable = true;
			path.add(table);
		}
	}
	
}
