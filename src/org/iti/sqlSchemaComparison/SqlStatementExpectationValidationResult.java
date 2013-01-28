package org.iti.sqlSchemaComparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;

public class SqlStatementExpectationValidationResult {

	private List<ISqlElement> missingTables = new ArrayList<>();
	
	public List<ISqlElement> getMissingTables() {
		return missingTables;
	}

	private List<ISqlElement> missingColumns = new ArrayList<>();

	public List<ISqlElement> getMissingColumns() {
		return missingColumns;
	}
	
	private Map<ISqlElement, List<List<ISqlElement>>> missingButReachableColumns = new HashMap<>();

	public Map<ISqlElement, List<List<ISqlElement>>> getMissingButReachableColumns() {
		return missingButReachableColumns;
	}
	
	public boolean isStatementValid() {
		return missingTables.size() == 0 && missingColumns.size() == 0 && missingButReachableColumns.size() == 0;
	}
	
	public SqlStatementExpectationValidationResult(List<ISqlElement> missingTables,
			List<ISqlElement> missingColumns,
			Map<ISqlElement, List<List<ISqlElement>>> missingButReachableColumns) {
		
		if (missingTables != null)
			this.missingTables = missingTables;
		
		if (missingColumns != null)
			this.missingColumns = missingColumns;
		
		if (missingButReachableColumns != null)
			this.missingButReachableColumns = missingButReachableColumns;
	}
}
