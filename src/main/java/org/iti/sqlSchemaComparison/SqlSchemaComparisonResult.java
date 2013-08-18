package org.iti.sqlSchemaComparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.iti.sqlSchemaComparison.edge.IForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;

public class SqlSchemaComparisonResult {

	private Map<ISqlElement, SchemaModification> modifications = new HashMap<>();
	
	public Map<ISqlElement, SchemaModification> getModifications() {
		return modifications;
	}

	public void addModification(ISqlElement modifiedElement, SchemaModification schemaModification) {
		modifications.put(modifiedElement, schemaModification);
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
	
	@Override
	public String toString() {
		String output = "";
		String result = "";
		
		output += "Schema Comparison Result\n";
		output += "------------------------\n";

		for (Entry<ISqlElement, SchemaModification> entry : modifications.entrySet()) {
			switch (entry.getValue()) {
				case CREATE_TABLE:
				case DELETE_TABLE:
				case RENAME_TABLE:
				case DELETE_AFTER_RENAME_TABLE:
					result += "\n";
					result += "----------------------\n";
					result += "| TABLE MODIFICATION |\n";
					result += "----------------------";
					break;

				case CREATE_COLUMN:
				case DELETE_COLUMN:
				case RENAME_COLUMN:
				case MOVE_COLUMN:
					result += "\n";
					result += "------------------------\n";
					result += "| COLUMN MODIFICATIONS |\n";
					result += "------------------------";
					break;
			
				default:
					break;
			}
			
			result += String.format("\n%s | %s", entry.getValue().toString(), entry.getKey().getSqlElementId());
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
			output += "\n-----------------------------\n";
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
			output += "\n";
			output += "-----------------------------\n";
			output += "| " + title + " |\n";
			output += "-----------------------------";
			output += result;
		}
		
		return output;
	}
	
}
