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
import java.util.Set;

import org.iti.sqlSchemaComparison.reachability.ISqlElementReachabilityChecker;
import org.iti.sqlSchemaComparison.reachability.SqlColumnReachableChecker;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public class SqlStatementExpectationValidator {

	private Graph<ISqlElement, DefaultEdge> schema;
	
	public SqlStatementExpectationValidator(Graph<ISqlElement, DefaultEdge> schema) {
		this.schema = schema;
	}

	public SqlStatementExpectationValidationResult computeGraphMatching(Graph<ISqlElement, DefaultEdge> expectedSchema) {		
		List<ISqlElement> missingTables = getMissingTables(expectedSchema);
		List<ISqlElement> missingColumns = getMissingColumns(expectedSchema);
		Map<ISqlElement, List<List<ISqlElement>>> missingButReachableColumns = getReachableColumns(expectedSchema, missingColumns);
		
		missingColumns.removeAll(missingButReachableColumns.keySet());
		
		return new SqlStatementExpectationValidationResult(missingTables, missingColumns, missingButReachableColumns);
	}

	private List<ISqlElement> getMissingTables(Graph<ISqlElement, DefaultEdge> expectedSchema) {
		List<ISqlElement> missingTables = new ArrayList<>();
		
		Set<ISqlElement> expectedTables = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, expectedSchema.vertexSet());
		Set<ISqlElement> currentTables = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet());
		
		for (ISqlElement expectedTable : expectedTables)
			if (!currentTables.contains(expectedTable))
				missingTables.add(expectedTable);
		
		return missingTables;
	}

	private List<ISqlElement> getMissingColumns(Graph<ISqlElement, DefaultEdge> expectedSchema) {
		List<ISqlElement> missingColumns = new ArrayList<>();
		
		Set<ISqlElement> expectedColumns = SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, expectedSchema.vertexSet());
		Set<ISqlElement> currentColumns = SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet());
		
		for (ISqlElement expectedColumn : expectedColumns)
			if (!currentColumns.contains(expectedColumn))
				missingColumns.add(expectedColumn);
		
		return missingColumns;
	}
	
	private Map<ISqlElement, List<List<ISqlElement>>> getReachableColumns(Graph<ISqlElement, DefaultEdge> expectedSchema,
			List<ISqlElement> missingColumns) {
		Map<ISqlElement, List<List<ISqlElement>>> reachableColumns = new HashMap<>();
		Set<ISqlElement> expectedTables = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, expectedSchema.vertexSet());
		
		for (ISqlElement column : missingColumns) {
			List<ISqlElement> matchingColumns = SqlElementFactory.getMatchingSqlColumns(column.getSqlElementId(), schema.vertexSet(), false);
			
			for (ISqlElement matchingColumn : matchingColumns) {
				for (ISqlElement table : expectedTables) {
					ISqlElement schemaTable = SqlElementFactory.getMatchingSqlElement(table, schema.vertexSet());
					
					if (schemaTable != null) {
						ISqlElementReachabilityChecker checker = new SqlColumnReachableChecker(schema, schemaTable, matchingColumn);
						
						if (checker.isReachable()) {
							if (!reachableColumns.containsKey(column))
								reachableColumns.put(column, new ArrayList<List<ISqlElement>>());
							
							reachableColumns.get(column).add(checker.getPath());
							break;
						}
					}
				}
			}
		}
		
		return reachableColumns;
	}
	
}
