/*
 *  Copyright 1999 Hagen Schink <hagen.schink@gmail.com>
 *
 *  This file is part of sql-schema-comparer.
 *
 *  sql-schema-comparer is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  sql-schema-comparer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with sql-schema-comparer.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

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
	
	@Override
	public String toString() {
		String output = "";
		String result = "";
		
		output += "Statement Comparison Result\n";
		output += "---------------------------\n";
		
		result += toResultString("MISSING TABLES", missingTables);
		result += toResultString("MISSING COLUMNS", missingColumns);
		result += toResultString("MISSING BUT REACHABLE COLUMNS", missingButReachableColumns);
		
		if (result.length() == 0)
			output += "Statement is valid!";
		else
			output += result;
		
		return output;
	}

	private String toResultString(String title, List<ISqlElement> elements) {
		String result = "";
		
		if (!elements.isEmpty()) {
			result += "\n";
			result += "------------------\n";
			result += "| " + title + " |\n";
			result += "------------------\n";
			for (ISqlElement t : elements)
				result += String.format("%s\n", t);
		}
		
		return result;
	}
	
	private String toResultString(String title, Map<ISqlElement, List<List<ISqlElement>>> elements) {
		String result = "";
		
		if (!elements.isEmpty()) {
			result += "\n";
			result += "---------------------------------\n";
			result += "| " + title + " |\n";
			result += "---------------------------------\n";
			for (ISqlElement c : elements.keySet())
				for (List<ISqlElement> p : elements.get(c))
					result += String.format("%s: %s\n", c, p);
		}
		
		return result;
	}
	
}
