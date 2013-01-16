package org.iti.sqlSchemaComparison;

import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;

public class SqlSchemaComparisonResult {

	private SqlTableVertex renamedTable;
	
	public SqlTableVertex getRenamedTable() {
		return renamedTable;
	}
	
	private SqlTableVertex removedTable;

	public SqlTableVertex getRemovedTable() {
		return removedTable;
	}
	
	private SqlTableVertex addedTable;

	public SqlTableVertex getAddedTable() {
		return addedTable;
	}
	
	private SqlColumnVertex renamedColumn;
	
	public SqlColumnVertex getRenamedColumn() {
		return renamedColumn;
	}
	
	private SqlColumnVertex movedColumn;
	
	public SqlColumnVertex getMovedColumn() {
		return movedColumn;
	}
	
	private SqlColumnVertex removedColumn;

	public SqlColumnVertex getRemovedColumn() {
		return removedColumn;
	}
	
	private SqlColumnVertex addedColumn;

	public SqlColumnVertex getAddedColumn() {
		return addedColumn;
	}
	
	public SqlSchemaComparisonResult(SqlTableVertex renamedTable, 
			SqlTableVertex removedTable,
			SqlTableVertex addedTable) {
		
		this.renamedTable = renamedTable;
		this.removedTable = removedTable;
		this.addedTable = addedTable;
	}
	
	public SqlSchemaComparisonResult(SqlColumnVertex renamedColumn,
			SqlColumnVertex movedColumn,
			SqlColumnVertex removedColumn,
			SqlColumnVertex addedColumn) {
		
		this.renamedColumn = renamedColumn;
		this.movedColumn = movedColumn;
		this.removedColumn = removedColumn;
		this.addedColumn = addedColumn;
	}
}
