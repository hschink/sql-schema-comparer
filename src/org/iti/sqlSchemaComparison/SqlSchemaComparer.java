package org.iti.sqlSchemaComparison;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public SqlSchemaComparisonResult comparisonResult = null;
	
	public SqlSchemaComparer(Graph<ISqlElement, DefaultEdge> schema1,
			Graph<ISqlElement, DefaultEdge> schema2) {
		
		this.schema1 = schema1;
		this.schema2 = schema2;
		
		computeIsomorphism();
		
		if (!isIsomorphic())
			computeGraphMatching();
		
		computeColumnTypeAndConstraintChanges();
	}

	private void computeIsomorphism() {
		EquivalenceComparator<ISqlElement, Graph<ISqlElement, DefaultEdge>> c = new SqlElementEquivalenceComparator();
		GraphIsomorphismInspector<Graph<ISqlElement, DefaultEdge>> inspector = AdaptiveIsomorphismInspectorFactory.createIsomorphismInspector(this.schema1, this.schema2, c, null);
		
		while (inspector.hasNext()) {
			isomorphisms.add((IsomorphismRelation<ISqlElement, Graph<ISqlElement, DefaultEdge>>)inspector.next());
		}
	}
	
	private void computeGraphMatching() {
		List<ISqlElement> vertices1 = new ArrayList<>();
		List<ISqlElement> vertices2 = new ArrayList<>();
		
		computeTableMatching(vertices1, vertices2);
		
		if (comparisonResult == null)
			computeColumnMatching(vertices1, vertices2);
		
		matching = new IsomorphismRelation<>(vertices1, vertices2, schema1, schema2);
	}

	private void computeTableMatching(List<ISqlElement> verticesList1, List<ISqlElement> verticesList2) {
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
			SqlTableVertex renamedTable = null;
			SqlTableVertex removedTable = null;
			SqlTableVertex addedTable = null;
			
			if (verticesWithNoMatch.size() > 2)
				throw new IllegalArgumentException("More than one table changed!");
			
			if (isSetRetained(tables1, tables2)) { // table rename
				Set<ISqlElement> tablesMatched = SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, vertices2);
				List<ISqlElement> tablesNotMatched = new ArrayList<>(tables2);
				
				tablesNotMatched.removeAll(tablesMatched);

				vertices1.add(verticesWithNoMatch.get(0));
				vertices2.add(tablesNotMatched.get(0));
				
				renamedTable = (SqlTableVertex)verticesWithNoMatch.get(0);
			} else if (isSetReduced(tables1, tables2)) {
				removedTable = (SqlTableVertex)verticesWithNoMatch.get(0);
			} else {
				addedTable = (SqlTableVertex)verticesWithNoMatch.get(0);
			}
			
			verticesList1.addAll(vertices1);
			verticesList2.addAll(vertices2);
			
			if (renamedTable != null || removedTable != null || addedTable != null)
				comparisonResult = new SqlSchemaComparisonResult(renamedTable, removedTable, addedTable);
		}
	}

	private void computeColumnMatching(List<ISqlElement> verticesList1, List<ISqlElement> verticesList2) {
		List<ISqlElement> vertices1 = new ArrayList<>();
		List<ISqlElement> vertices2 = new ArrayList<>();
		List<ISqlElement> verticesWithNoMatch = new ArrayList<>();
		SqlColumnVertex movedColumn = null;
		SqlColumnVertex renamedColumn = null;
		SqlColumnVertex removedColumn = null;
		SqlColumnVertex addedColumn = null;
		
		Set<ISqlElement> columns1 = SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet());
		Set<ISqlElement> columns2 = SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet());
		
		for (ISqlElement column1 : columns1) {
			if (columns2.contains(column1)) {
				ISqlElement column2 = SqlElementFactory.getMatchingSqlElement(column1, columns2);
				
				vertices1.add(column1);
				vertices2.add(column2);
				
				if (!isTableMatching(column1, column2, schema1, schema2)) {
					movedColumn = (SqlColumnVertex)column1;
				}
			} else {
				verticesWithNoMatch.add(column1);
			}
		}
		
		List<ISqlElement> missingColumns = new ArrayList<>(columns2);
		
		missingColumns.removeAll(vertices2);
		verticesWithNoMatch.addAll(missingColumns);
		
		if (movedColumn == null && verticesWithNoMatch.size() > 0) {
			
			if (verticesWithNoMatch.size() > 2)
				throw new IllegalArgumentException("More than one column changed!");
			
			if (isSetRetained(columns1, columns2)) { // column renamed
				
				if (verticesWithNoMatch.size() != 2)
					throw new IllegalArgumentException("Illegal number of columns without match!");

				renamedColumn = (SqlColumnVertex) verticesWithNoMatch.get(0);
				
				vertices1.add(verticesWithNoMatch.get(0));
				vertices2.add(verticesWithNoMatch.get(1));
					
			} else if (isSetReduced(columns1, columns2)) {
				removedColumn = (SqlColumnVertex)verticesWithNoMatch.get(0);
			} else {
				addedColumn = (SqlColumnVertex)verticesWithNoMatch.get(0);
			}
		}
		
		verticesList1.addAll(vertices1);
		verticesList2.addAll(vertices2);
		
		if (renamedColumn != null || movedColumn != null || removedColumn != null || addedColumn != null)
			comparisonResult = new SqlSchemaComparisonResult(renamedColumn, movedColumn, removedColumn, addedColumn);
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
			
			if (vertex2 != null)
				columnComparisonResults.put(vertex2, ColumnConstraintHelper.compare(vertex1, vertex2));
		}
		
		for (ISqlElement vertex2 : SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet())) {
			ISqlElement vertex1 = getMatchingVertex(vertex2, false);
			
			if (vertex1 != null)
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
	
}
