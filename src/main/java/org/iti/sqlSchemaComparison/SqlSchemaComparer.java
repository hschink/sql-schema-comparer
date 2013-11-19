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
import java.util.Map.Entry;
import java.util.Set;

import org.iti.graph.StructureGraph;
import org.iti.graph.comparison.IStructureGraphComparer;
import org.iti.graph.comparison.StructureGraphComparer;
import org.iti.graph.comparison.StructureGraphComparisonException;
import org.iti.graph.comparison.result.IStructureModification;
import org.iti.graph.comparison.result.StructureGraphComparisonResult;
import org.iti.graph.comparison.result.Type;
import org.iti.graph.nodes.IStructureElement;
import org.iti.sqlSchemaComparison.edge.IForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.ColumnConstraintHelper;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class SqlSchemaComparer {

	private static final IStructureGraphComparer comparer = new StructureGraphComparer();
	
	private StructureGraph graph1;
	private StructureGraph graph2;
	
	public SqlSchemaComparisonResult comparisonResult = new SqlSchemaComparisonResult();

	public boolean isIsomorphic() {
		return comparisonResult.getModifications().size() == 0;
	}
	
	public SqlSchemaComparer(DirectedGraph<IStructureElement, DefaultEdge> schema1,
			DirectedGraph<IStructureElement, DefaultEdge> schema2) throws StructureGraphComparisonException {
		graph1 = new StructureGraph(schema1);
		graph2 = new StructureGraph(schema2);

		StructureGraphComparisonResult result = comparer.compare(graph1, graph2);

		setSqlSchemaComparisonResult(result);

		computeColumnTypeAndConstraintChanges(result, schema1);

		computeForeignKeyChanges(schema1, schema2);
	}

	private void setSqlSchemaComparisonResult(
			StructureGraphComparisonResult result) {
		
		for (Entry<String, IStructureModification> entry : result.getNodeModifications().entrySet()) {
			Type type = entry.getValue().getType();
			ISqlElement currentSqlElement = getCurrentSqlElement(entry.getKey(), type);

			comparisonResult.addModification(currentSqlElement, getModificationType(currentSqlElement, type));
		}
	}

	private void computeColumnTypeAndConstraintChanges(StructureGraphComparisonResult result, DirectedGraph<IStructureElement, DefaultEdge> schema1) {
        Map<ISqlElement, SqlSchemaColumnComparisonResult> columnComparisonResults = compareUnchagedColumns(schema1);

        columnComparisonResults.putAll(compareRenamedColumns(result));
        
        comparisonResult.setColumnComparisonResults(columnComparisonResults);
	}

	private Map<ISqlElement, SqlSchemaColumnComparisonResult> compareUnchagedColumns(DirectedGraph<IStructureElement, DefaultEdge> schema1) {
		Map<ISqlElement, SqlSchemaColumnComparisonResult> columnComparisonResults = new HashMap<>();
        
        for (ISqlElement vertex1 : SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet())) {
        	String identifier = graph1.getIdentifier(vertex1);
            ISqlElement vertex2 = (ISqlElement) graph2.getStructureElement(identifier);
                
            if (vertex2 != null && !columnComparisonResults.containsKey(vertex2))
                columnComparisonResults.put(vertex2, ColumnConstraintHelper.compare(vertex1, vertex2));
        }
		return columnComparisonResults;
	}

	private Map<ISqlElement, SqlSchemaColumnComparisonResult> compareRenamedColumns(StructureGraphComparisonResult result) {
		Map<ISqlElement, SqlSchemaColumnComparisonResult> columnComparisonResults = new HashMap<>();

		for (Entry<String, IStructureModification> entry : result.getNodeModifications().entrySet()) {
			ISqlElement current = (ISqlElement) graph2.getStructureElement(entry.getKey());

			if (current instanceof SqlColumnVertex) {
				switch (entry.getValue().getType()) {
					case NodeMoved:
					case NodeRenamed:
						String originalIdentifier = entry.getValue().getModificationDetail().getIdentifier();
						ISqlElement original = (ISqlElement) graph1.getStructureElement(originalIdentifier);
					
			            if (original != null && !columnComparisonResults.containsKey(original))
			                columnComparisonResults.put(current, ColumnConstraintHelper.compare(original, current));
	
						break;
	
					default:
						break;
				}
			}
		}

		return columnComparisonResults;
	}

	private ISqlElement getCurrentSqlElement(String identifier, Type type) {
		IStructureElement element = null;

		switch (type) {
			case NodeDeleted:
				element = graph1.getStructureElement(identifier);
				break;

			default:
				element = graph2.getStructureElement(identifier);
				break;
		}

		return (ISqlElement)element;
	}

	private SchemaModification getModificationType(ISqlElement sqlElement,
			Type modificationType) {
		if (sqlElement instanceof SqlTableVertex) {
			switch (modificationType) {
				case NodeAdded: return SchemaModification.CREATE_TABLE;
				case NodeDeleted: return SchemaModification.DELETE_TABLE;

				default: return SchemaModification.RENAME_TABLE;
			}
		} else {
			switch (modificationType) {
				case NodeAdded: return SchemaModification.CREATE_COLUMN;
				case NodeDeleted: return SchemaModification.DELETE_COLUMN;
				case NodeMoved: return SchemaModification.MOVE_COLUMN;

				default: return SchemaModification.RENAME_COLUMN;
			}
		}
	}

	private void computeForeignKeyChanges(DirectedGraph<IStructureElement,DefaultEdge> schema1,
			DirectedGraph<IStructureElement,DefaultEdge> schema2) {
        List<IForeignKeyRelationEdge> allForeignKeyRelations = getForeignKeyRelations(schema1.edgeSet());
        List<IForeignKeyRelationEdge> addedForeignKeyRelations = getForeignKeyRelations(schema2.edgeSet());
        List<IForeignKeyRelationEdge> removedForeignKeyRelations = getForeignKeyRelations(schema1.edgeSet());
        
        removedForeignKeyRelations.removeAll(addedForeignKeyRelations);
        addedForeignKeyRelations.removeAll(allForeignKeyRelations);
        
        if (comparisonResult == null) {
	        comparisonResult = new SqlSchemaComparisonResult();
		}
        
        comparisonResult.setAddedForeignKeyRelations(addedForeignKeyRelations);
        comparisonResult.setRemovedForeignKeyRelations(removedForeignKeyRelations);
	}

	private List<IForeignKeyRelationEdge> getForeignKeyRelations(Set<DefaultEdge> edges) {
        List<IForeignKeyRelationEdge> list = new ArrayList<>();
        
        for (DefaultEdge edge : edges) {
            if (edge instanceof IForeignKeyRelationEdge) {
                list.add((IForeignKeyRelationEdge) edge);
            }
        }
        
        return list;
	}
}
