package org.iti.sqlSchemaComparison;

import java.util.HashMap;
import java.util.Map;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
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
	
	private Map<ISqlElement, SqlSchemaColumnComparisonResult> columnComparisonResults = new HashMap<>();
	
	public Map<ISqlElement, SqlSchemaColumnComparisonResult> getColumnComparisonResults() {
		return columnComparisonResults;
	}

	public void setColumnComparisonResults(
			Map<ISqlElement, SqlSchemaColumnComparisonResult> columnComparisonResults) {
		this.columnComparisonResults = columnComparisonResults;
	}

	public SqlSchemaComparisonResult() {}
	
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
