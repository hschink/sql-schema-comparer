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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iti.sqlSchemaComparison.frontends.ISqlQueryFrontend.QueryType;
import org.iti.sqlSchemaComparison.reachability.ISqlElementReachabilityChecker;
import org.iti.sqlSchemaComparison.reachability.SqlColumnReachableChecker;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.structureGraph.StructureGraph;
import org.iti.structureGraph.comparison.StatementStructureGraphComparer;
import org.iti.structureGraph.comparison.StructureGraphComparisonException;
import org.iti.structureGraph.comparison.result.StructureGraphComparisonResult;
import org.iti.structureGraph.comparison.result.Type;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class SqlStatementExpectationValidator {

	private DirectedGraph<IStructureElement, DefaultEdge> schema;

	public SqlStatementExpectationValidator(DirectedGraph<IStructureElement, DefaultEdge> schema) {
		this.schema = schema;
	}

	public SqlStatementExpectationValidationResult computeGraphMatching(DirectedGraph<IStructureElement, DefaultEdge> statement) {
		return computeGraphMatching(statement, QueryType.DML);
	}

	public SqlStatementExpectationValidationResult computeGraphMatching(
			DirectedGraph<IStructureElement, DefaultEdge> statement, QueryType queryType) {
		StructureGraph schemaGraph = new StructureGraph(schema);
		StructureGraph expectedSchemaGraph = new StructureGraph(statement);
		StatementStructureGraphComparer comparer = new StatementStructureGraphComparer();

        StructureGraphComparisonResult result = null;
		try {
			result = comparer.compare(expectedSchemaGraph, schemaGraph, queryType.equals(QueryType.DQL));
		} catch (StructureGraphComparisonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        List<ISqlElement> missingTables = getMissingElementsByType(result.getElementsByModification(Type.NodeDeleted), SqlTableVertex.class);
        List<ISqlElement> missingColumns = getMissingElementsByType(result.getElementsByModification(Type.NodeDeleted), SqlColumnVertex.class);
		Map<ISqlElement, List<List<ISqlElement>>> missingButReachableColumns = getReachableColumns(statement, missingColumns);

		missingColumns.addAll(getMissingElementsByType(result.getElementsByModification(Type.NodeAdded), SqlColumnVertex.class));

		missingColumns.removeAll(missingButReachableColumns.keySet());

		return new SqlStatementExpectationValidationResult(missingTables, missingColumns, missingButReachableColumns);
	}

	private List<ISqlElement> getMissingElementsByType(
			Collection<IStructureElement> elements,
			Class<?> class1) {
		List<ISqlElement> missingElemets = new ArrayList<>();

		for (IStructureElement element : elements) {
			if (class1.isInstance(element)) {
				missingElemets.add((ISqlElement)element);
			}
		}

		return missingElemets;
	}

	private Map<ISqlElement, List<List<ISqlElement>>> getReachableColumns(DirectedGraph<IStructureElement, DefaultEdge> expectedSchema,
			List<ISqlElement> missingColumns) {
		Map<ISqlElement, List<List<ISqlElement>>> reachableColumns = new HashMap<>();
		Set<ISqlElement> expectedTables = SqlElementFactory.getSqlElementsOfType(SqlTableVertex.class, expectedSchema.vertexSet());

		for (ISqlElement column : missingColumns) {
			List<ISqlElement> matchingColumns = SqlElementFactory.getMatchingSqlColumns(column.getName(), schema.vertexSet(), false);

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
