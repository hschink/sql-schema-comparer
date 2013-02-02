package org.iti.sqlSchemaComparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iti.sqlSchemaComparison.edge.IForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;

public class SqlSchemaComparisonResult {

	private SqlTableVertex renamedTable;
	
	public SqlTableVertex getRenamedTable() {
		return renamedTable;
	}
	
	private SqlTableVertex removedRenamedTable;
	
	public SqlTableVertex getRemovedRenamedTable() {
		return removedRenamedTable;
	}
	
	private SqlTableVertex removedTable;

	public SqlTableVertex getRemovedTable() {
		return removedTable;
	}

	public void setRemovedColumn(SqlColumnVertex removedColumn) {
		this.removedColumn = removedColumn;
	}
	
	private SqlTableVertex addedTable;

	public SqlTableVertex getAddedTable() {
		return addedTable;
	}

	public void setAddedColumn(SqlColumnVertex addedColumn) {
		this.addedColumn = addedColumn;
	}
	
	private SqlColumnVertex renamedColumn;

	public SqlColumnVertex getRenamedColumn() {
		return renamedColumn;
	}
	
	public void setRenamedColumn(SqlColumnVertex renamedColumn) {
		this.renamedColumn = renamedColumn;
	}
	
	private SqlColumnVertex movedColumn;
	
	public SqlColumnVertex getMovedColumn() {
		return movedColumn;
	}

	public void setMovedColumn(SqlColumnVertex movedColumn) {
		this.movedColumn = movedColumn;
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
	
	private List<IForeignKeyRelationEdge> addedForeignKeyRelations = new ArrayList<>();
	
	public List<IForeignKeyRelationEdge> getAddedForeignKeyRelations() {
		return addedForeignKeyRelations;
	}

	public void setAddedForeignKeyRelations(
			List<IForeignKeyRelationEdge> addedForeignKeyRelations) {
		this.addedForeignKeyRelations = addedForeignKeyRelations;
	}

	private List<IForeignKeyRelationEdge> removedForeignKeyRelations = new ArrayList<>();

	public List<IForeignKeyRelationEdge> getRemovedForeignKeyRelations() {
		return removedForeignKeyRelations;
	}

	public void setRemovedForeignKeyRelations(
			List<IForeignKeyRelationEdge> removedForeignKeyRelations) {
		this.removedForeignKeyRelations = removedForeignKeyRelations;
	}

	public SqlSchemaComparisonResult() {}
	
	public SqlSchemaComparisonResult(SqlTableVertex renamedTable,
			SqlTableVertex removedRenamedTable,
			SqlTableVertex removedTable,
			SqlTableVertex addedTable) {
		
		this.renamedTable = renamedTable;
		this.removedRenamedTable = removedRenamedTable;
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
	
	@Override
	public String toString() {
		String output = "";
		String result = "";
		
		output += "Schema Comparison Result\n";
		output += "------------------------\n";
		
		if (renamedTable != null || removedRenamedTable != null || removedTable != null || addedTable != null) {
			result += "\n";
			result += "-----------------------\n";
			result += "| TABLE MODIFICATIONS |\n";
			result += "-----------------------";
			if (renamedTable != null)
				result += String.format("\nRenamed Table | %s", renamedTable.getSqlElementId());
			
			if (removedRenamedTable != null)
				result += String.format("\nRenamed Table | %s", removedRenamedTable.getSqlElementId());
			
			if (removedTable != null)
				result += String.format("\nRemoved Table | %s", removedTable.getSqlElementId());
			
			if (addedTable != null)
				result += String.format("\nCreated Table | %s", addedTable.getSqlElementId());
		}
		
		if (renamedColumn != null || movedColumn != null || removedColumn != null || addedColumn != null) {
			result += "\n";
			result += "------------------------\n";
			result += "| COLUMN MODIFICATIONS |\n";
			result += "------------------------";
			
			if (renamedColumn != null)
				result += String.format("\nRenamed Column | %s", renamedColumn.getSqlElementId());
			
			if (movedColumn != null)
				result += String.format("\nMoved Column   | %s", movedColumn.getSqlElementId());
			
			if (removedColumn != null)
				result += String.format("\nRemoved Column | %s", removedColumn.getSqlElementId());
			
			if (addedColumn != null)
				result += String.format("\nCreated Column | %s", addedColumn.getSqlElementId());
		}
		
		result += toResultString("COLUMN COMPARISON RESULTS", columnComparisonResults);
		result += toResultString("CREATED FOREIGN REFERENCES", addedForeignKeyRelations);
		result += toResultString("REMOVED FOREIGN REFERENCES", removedForeignKeyRelations);
		
		if (result.length() == 0)
			output += "Schemas are isomorphic!";
		else
			output += result;
		
		return output;
	}

	private String toResultString(String title, Map<ISqlElement, SqlSchemaColumnComparisonResult> elements) {
		String output = "";
		String result = "";
		
		if (!elements.isEmpty()) {
			for (ISqlElement r : elements.keySet()) {
				String columnComparisonResult = elements.get(r).toString();
				
				if (columnComparisonResult.length() != 0)
					result += String.format("--- %s ---\n%s", r.toString(), columnComparisonResult);
			}
		}
		
		if (result.length() > 0) {
			output += "-----------------------------\n";
			output += "| " + title + " |\n";
			output += "-----------------------------\n";
			output += result;
		}
		
		return output;
	}

	private String toResultString(String title, List<IForeignKeyRelationEdge> elements) {
		String output = "";
		String result = "";
		
		if (!elements.isEmpty()) {
			for (IForeignKeyRelationEdge r : elements)
				result += String.format("\n%s -> %s", r.getReferencingColumn(), r.getForeignKeyColumn());
		}
		
		if (result.length() > 0) {
			output += "-----------------------------\n";
			output += "| " + title + " |\n";
			output += "-----------------------------";
			output += result;
		}
		
		return output;
	}
	
}
