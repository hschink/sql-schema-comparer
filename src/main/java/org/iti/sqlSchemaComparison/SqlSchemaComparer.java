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
import java.util.Map.Entry;
import java.util.Set;

import org.iti.sqlSchemaComparison.edge.IForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.edge.ITableHasColumnEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.ColumnConstraintHelper;
import org.jgrapht.Graph;
import org.jgrapht.GraphMapping;
import org.jgrapht.experimental.equivalence.EquivalenceComparator;
import org.jgrapht.experimental.isomorphism.AdaptiveIsomorphismInspectorFactory;
import org.jgrapht.experimental.isomorphism.GraphIsomorphismInspector;
import org.jgrapht.experimental.isomorphism.IsomorphismRelation;
import org.jgrapht.graph.DefaultEdge;

public class SqlSchemaComparer {

	private Graph<ISqlElement, DefaultEdge> schema1;
	private Graph<ISqlElement, DefaultEdge> schema2;
	
	private List<IsomorphismRelation<ISqlElement, Graph<ISqlElement, DefaultEdge>>> isomorphisms = new ArrayList<>();
	
	public boolean isIsomorphic() {
		return isomorphisms.size() > 0;
	}

	public List<IsomorphismRelation<ISqlElement, Graph<ISqlElement, DefaultEdge>>> getIsomorphisms() {
		return isomorphisms;
	}
	
	public GraphMapping<ISqlElement, DefaultEdge> matching = null;

	public SqlSchemaComparisonResult comparisonResult = new SqlSchemaComparisonResult();
	
	public SqlSchemaComparer(Graph<ISqlElement, DefaultEdge> schema1,
			Graph<ISqlElement, DefaultEdge> schema2) {
		
		this.schema1 = schema1;
		this.schema2 = schema2;
		
		computeIsomorphism();
		
		if (!isIsomorphic())
			computeGraphMatching();
		
		computeColumnTypeAndConstraintChanges();
		
		computeForeignKeyChanges();
	}

	private void computeIsomorphism() {
		EquivalenceComparator<ISqlElement, Graph<ISqlElement, DefaultEdge>> c = new SqlElementEquivalenceComparator();
		
		@SuppressWarnings("unchecked")
		GraphIsomorphismInspector<Graph<ISqlElement, DefaultEdge>> inspector = AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(this.schema1, this.schema2, c, null);
		
		while (inspector.hasNext()) {
			@SuppressWarnings("unchecked")
			IsomorphismRelation<ISqlElement, Graph<ISqlElement, DefaultEdge>> isomorphism = (IsomorphismRelation<ISqlElement, Graph<ISqlElement, DefaultEdge>>)inspector.next();
			
			isomorphisms.add(isomorphism);
		}
	}
	
	private void computeGraphMatching() {
		List<ISqlElement> vertices1 = new ArrayList<>();
		List<ISqlElement> vertices2 = new ArrayList<>();
		
		computeTableMatching(vertices1, vertices2);
		
		computeColumnMatching(vertices1, vertices2);
		
		matching = new IsomorphismRelation<>(vertices1, vertices2, schema1, schema2);
	}

	private void computeTableMatching(List<ISqlElement> verticesList1, List<ISqlElement> verticesList2) {
		Map<ISqlElement, SchemaModification> modifications = new HashMap<>();

		List<ISqlElement> vertices1 = new ArrayList<>();
		List<ISqlElement> vertices2 = new ArrayList<>();
		List<ISqlElement> verticesWithNoMatch = new ArrayList<>();
		
		Set<ISqlElement> tables1 = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema1.vertexSet());
		Set<ISqlElement> tables2 = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema2.vertexSet());
		
		for (ISqlElement table1 : tables1) {
			if (tables2.contains(table1)) {
				vertices1.add(table1);
				vertices2.add(SqlElementFactory.getMatchingSqlElement(table1, tables2));
			} else {
				verticesWithNoMatch.add(table1);
			}
		}
		
		List<ISqlElement> missingTables = new ArrayList<>(tables2);
		
		missingTables.removeAll(vertices2);
		verticesWithNoMatch.addAll(missingTables);
		
		if (verticesWithNoMatch.size() > 0) {
			if (verticesWithNoMatch.size() > 2)
				throw new IllegalArgumentException("More than one table changed!");
			
			if (isSetRetained(tables1, tables2)) { // table rename
				Set<ISqlElement> tablesMatched = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, vertices2);
				List<ISqlElement> tablesNotMatched = new ArrayList<>(tables2);
				
				tablesNotMatched.removeAll(tablesMatched);

				vertices1.add(verticesWithNoMatch.get(0));
				vertices2.add(tablesNotMatched.get(0));

				modifications.put(verticesWithNoMatch.get(0), SchemaModification.RENAME_TABLE);
				modifications.put(tablesNotMatched.get(0), SchemaModification.DELETE_AFTER_RENAME_TABLE);
			} else if (isSetReduced(tables1, tables2)) {
				modifications.put(verticesWithNoMatch.get(0), SchemaModification.DELETE_TABLE);
			} else {
				modifications.put(verticesWithNoMatch.get(0), SchemaModification.CREATE_TABLE);
			}
			
			verticesList1.addAll(vertices1);
			verticesList2.addAll(vertices2);
			
			if (modifications.isEmpty()) {
				comparisonResult.addModification(null, SchemaModification.NO_MODIFICATION);
			} else {
				for (Entry<ISqlElement, SchemaModification> entry : modifications.entrySet()) {
					comparisonResult.addModification(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private void computeColumnMatching(List<ISqlElement> verticesList1, List<ISqlElement> verticesList2) {
		SchemaModification schemaModification = SchemaModification.NO_MODIFICATION;
		ISqlElement modifiedElement = null;

		List<ISqlElement> vertices1 = new ArrayList<>();
		List<ISqlElement> vertices2 = new ArrayList<>();
		List<ISqlElement> verticesWithNoMatch = new ArrayList<>();

		Set<ISqlElement> columns1 = SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet());
		Set<ISqlElement> columns2 = SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet());
		
		for (ISqlElement column1 : columns1) {
			if (!isColumnOfAddedTable(column1) && !isColumnOfRemovedTable(column1) && !isColumnOfRenamedTable(column1)) {	
				if (columns2.contains(column1)) {
					ISqlElement column2 = SqlElementFactory.getMatchingSqlElement(column1, columns2);
					
					vertices1.add(column1);
					vertices2.add(column2);
				} else {				
					verticesWithNoMatch.add(column1);
				}
			}
		}
		
		List<ISqlElement> missingColumns = new ArrayList<>(columns2);
		List<ISqlElement> columnsOfChangedTables = new ArrayList<>();
		
		for (ISqlElement column : missingColumns)
			if (isColumnOfAddedTable(column) || isColumnOfRemovedTable(column) || isColumnOfRenamedTable(column))
				columnsOfChangedTables.add(column);
		
		missingColumns.removeAll(columnsOfChangedTables);
		
		missingColumns.removeAll(vertices2);
		verticesWithNoMatch.addAll(missingColumns);
		
		if (verticesWithNoMatch.size() > 0) {
			
			if (verticesWithNoMatch.size() > 2)
				throw new IllegalArgumentException("More than one column changed!");
			
			if (isSetRetained(columns1, columns2)) { // column renamed or moved
				
				if (verticesWithNoMatch.size() != 2)
					throw new IllegalArgumentException("Illegal number of columns without match!");

				SqlColumnVertex column1 = (SqlColumnVertex) verticesWithNoMatch.get(0);
				SqlColumnVertex column2 = (SqlColumnVertex) verticesWithNoMatch.get(1);
				
				if (column1.getSqlElementId().equals(column2.getSqlElementId())
						&& !isTableMatching(column1, column2, schema1, schema2)) {
					modifiedElement = (SqlColumnVertex)column1;
					schemaModification = SchemaModification.MOVE_COLUMN;
				} else {
					modifiedElement = (SqlColumnVertex) verticesWithNoMatch.get(0);
					schemaModification = SchemaModification.RENAME_COLUMN;
				}
				
				vertices1.add(verticesWithNoMatch.get(0));
				vertices2.add(verticesWithNoMatch.get(1));
					
			} else if (columns1.contains(verticesWithNoMatch.get(0))) {
				modifiedElement = (SqlColumnVertex)verticesWithNoMatch.get(0);
				schemaModification = SchemaModification.DELETE_COLUMN;
			} else {
				modifiedElement = (SqlColumnVertex)verticesWithNoMatch.get(0);
				schemaModification = SchemaModification.CREATE_COLUMN;
			}
		}
		
		verticesList1.addAll(vertices1);
		verticesList2.addAll(vertices2);

		if (modifiedElement != null)
			comparisonResult.addModification(modifiedElement, schemaModification);
	}
	
	private boolean isColumnOfAddedTable(ISqlElement column) {
		for (Entry<ISqlElement, SchemaModification> entry : comparisonResult.getModifications().entrySet()) {
			if (entry.getValue() == SchemaModification.CREATE_TABLE
					&& isTableOfColumn((SqlTableVertex) entry.getKey(), ((SqlColumnVertex) column))) {
				return true;
			}
		}

		return false;
	}
	
	private boolean isColumnOfRemovedTable(ISqlElement column) {
		for (Entry<ISqlElement, SchemaModification> entry : comparisonResult.getModifications().entrySet()) {
			if (entry.getValue() == SchemaModification.DELETE_TABLE
					&& isTableOfColumn((SqlTableVertex) entry.getKey(), ((SqlColumnVertex) column))) {
				return true;
			}
		}

		return false;
	}
	
	private boolean isColumnOfRenamedTable(ISqlElement column) {
		for (Entry<ISqlElement, SchemaModification> entry : comparisonResult.getModifications().entrySet()) {
			if ((entry.getValue() == SchemaModification.RENAME_TABLE
					|| entry.getValue() == SchemaModification.DELETE_AFTER_RENAME_TABLE)
					&& isTableOfColumn((SqlTableVertex) entry.getKey(), ((SqlColumnVertex) column))) {
				return true;
			}
		}

		return false;
	}
	
	private boolean isTableOfColumn(SqlTableVertex table, SqlColumnVertex column) {
		return table.getSqlElementId().equals(column.getTable());
	}

	private static boolean isTableMatching(ISqlElement column1, ISqlElement column2,
			Graph<ISqlElement, DefaultEdge> schema1,
			Graph<ISqlElement, DefaultEdge> schema2) {

		ISqlElement table1 = getTableVertex(column1, schema1);
		ISqlElement table2 = getTableVertex(column2, schema2);
		
		if (table1 == null || table2 == null)
			return false;
		
		return table1.equals(table2);
	}
	
	private static ISqlElement getTableVertex(ISqlElement column,
			Graph<ISqlElement, DefaultEdge> schema) {
		
		Set<ISqlElement> tables = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet());
		
		for (ISqlElement table : tables) {
			DefaultEdge edge = schema.getEdge(column, table);
			
			if (edge instanceof ITableHasColumnEdge && edge != null)
				return table;
		}
		
		return null;
	}

	private boolean isSetRetained(Collection<?> vertices1,
			Collection<?> vertices2) {
		return vertices1.size() == vertices2.size();
	}

	private boolean isSetReduced(Collection<?> vertices1,
			Collection<?> vertices2) {
		return vertices1.size() > vertices2.size();
	}

	private void computeColumnTypeAndConstraintChanges() {
		Map<ISqlElement, SqlSchemaColumnComparisonResult> columnComparisonResults = new HashMap<>();
		
		for (ISqlElement vertex1 : SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet())) {
			ISqlElement vertex2 = getMatchingVertex(vertex1, true);
			
			if (vertex2 != null && !columnComparisonResults.containsKey(vertex2))
				columnComparisonResults.put(vertex2, ColumnConstraintHelper.compare(vertex1, vertex2));
		}
		
		for (ISqlElement vertex2 : SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet())) {
			ISqlElement vertex1 = getMatchingVertex(vertex2, false);
			
			if (vertex1 != null && !columnComparisonResults.containsKey(vertex1))
				columnComparisonResults.put(vertex1, ColumnConstraintHelper.compare(vertex1, vertex2));
		}
		
		if (comparisonResult == null)
			comparisonResult = new SqlSchemaComparisonResult();
		
		comparisonResult.setColumnComparisonResults(columnComparisonResults);
	}

	private ISqlElement getMatchingVertex(ISqlElement vertex1, boolean forward) {

		if (isIsomorphic())
			return isomorphisms.get(0).getVertexCorrespondence(vertex1, forward);
		
		return matching.getVertexCorrespondence(vertex1, forward);
	}
	
	private void computeForeignKeyChanges() {
		List<IForeignKeyRelationEdge> allForeignKeyRelations = getForeignKeyRelations(schema1.edgeSet());
		List<IForeignKeyRelationEdge> addedForeignKeyRelations = getForeignKeyRelations(schema2.edgeSet());
		List<IForeignKeyRelationEdge> removedForeignKeyRelations = getForeignKeyRelations(schema1.edgeSet());
		
		removedForeignKeyRelations.removeAll(addedForeignKeyRelations);
		addedForeignKeyRelations.removeAll(allForeignKeyRelations);
		
		if (comparisonResult == null)
			comparisonResult = new SqlSchemaComparisonResult();
		
		comparisonResult.setAddedForeignKeyRelations(addedForeignKeyRelations);
		comparisonResult.setRemovedForeignKeyRelations(removedForeignKeyRelations);
	}

	private List<IForeignKeyRelationEdge> getForeignKeyRelations(Set<DefaultEdge> edges) {
		List<IForeignKeyRelationEdge> list = new ArrayList<>();
		
		for (DefaultEdge edge : edges)
			if (edge instanceof IForeignKeyRelationEdge)
				list.add((IForeignKeyRelationEdge) edge);
		
		return list;
	}
	
}
