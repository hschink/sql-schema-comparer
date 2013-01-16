package org.iti.sqlSchemaComparison.edge;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
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
	
	public TableHasColumnEdge(ISqlElement table, ISqlElement column) {
		this.table = (SqlTableVertex) table;
		this.column = (SqlColumnVertex) column;
	}

}
