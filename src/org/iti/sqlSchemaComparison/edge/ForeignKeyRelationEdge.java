package org.iti.sqlSchemaComparison.edge;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
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
	
	public ForeignKeyRelationEdge(ISqlElement referencingColumn, ISqlElement foreignKeyTable, ISqlElement foreignKeyColumn) {
		this.referencingColumn = (SqlColumnVertex) referencingColumn;
		this.foreignKeyTable = (SqlTableVertex) foreignKeyTable;
		this.foreignKeyColumn = (SqlColumnVertex) foreignKeyColumn;
	}

}
