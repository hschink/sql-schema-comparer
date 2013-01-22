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
	
	@Override
	public String toString() {
		return referencingColumn.getSqlElementId() + " --> " 
				+ foreignKeyTable.getSqlElementId() 
				+ " (" + foreignKeyColumn.getSqlElementId() + ")";
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
