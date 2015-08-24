package org.iti.sqlSchemaComparison;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint.ConstraintType;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class TestHelper {

	public static List<IStructureElement> getColumnWithConstraint(DirectedGraph<IStructureElement,DefaultEdge> schema, ConstraintType constraintType) {
		List<IStructureElement> columns = new ArrayList<>();

		for (IStructureElement e : schema.vertexSet()) {
			if (IColumnConstraint.class.isAssignableFrom(e.getClass())
					&& ((IColumnConstraint) e).getConstraintType().equals(constraintType)) {
				Set<DefaultEdge> incomingEdges = schema.incomingEdgesOf(e);
				IStructureElement source = schema.getEdgeSource(incomingEdges.iterator().next());

				assertTrue(source.getClass().isAssignableFrom(SqlColumnVertex.class));

				columns.add(source);
			}
		}

		return columns;
	}

	public static Entry<ISqlElement, SchemaModification> getModificationOfType(SqlSchemaComparisonResult result, SchemaModification schemaModification) {
		for (Entry<ISqlElement, SchemaModification> e : result.getModifications().entrySet()) {
			if (e.getValue().equals(schemaModification)) {
				return e;
			}
		}

		return null;
	}

}
