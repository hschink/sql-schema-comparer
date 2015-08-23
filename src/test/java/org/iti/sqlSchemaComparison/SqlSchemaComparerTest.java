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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.iti.sqlSchemaComparison.edge.ForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.DefaultColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.NotNullColumnConstraint;
import org.iti.structureGraph.comparison.StructureGraphComparisonException;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqlSchemaComparerTest {

	private static DirectedGraph<IStructureElement, DefaultEdge> schema1 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema2 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema3 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema4 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema5 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema6 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema7 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema8 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema9 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);

	private static DirectedGraph<IStructureElement, DefaultEdge> schema11 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema12 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
	private static DirectedGraph<IStructureElement, DefaultEdge> schema13 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);

	private static DirectedGraph<IStructureElement, DefaultEdge> schema31 = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);

	private static ISqlElement t1 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t1");
	private static ISqlElement t2 = SqlElementFactory.createSqlElement(SqlElementType.Table, "t2");
	
	private static ISqlElement c1 = new SqlColumnVertex("c1", "", t1.getSqlElementId());
	private static ISqlElement c12 = new SqlColumnVertex("c12", "", t1.getSqlElementId());
	private static ISqlElement c2 = new SqlColumnVertex("c2", "", t2.getSqlElementId());
	private static ISqlElement c22 = new SqlColumnVertex("c22", "", t2.getSqlElementId());
	private static ISqlElement c3 = new SqlColumnVertex("c1", "", t2.getSqlElementId());
	
	private static ISqlElement c111 = new SqlColumnVertex("c1", "INTEGER", t1.getSqlElementId());
	private static ISqlElement c112 = new SqlColumnVertex("c1", "FLOAT", t1.getSqlElementId());
	private static ISqlElement c113 = new SqlColumnVertex("c1", "INTEGER", t1.getSqlElementId());
	
	private static IColumnConstraint constraint1 = new DefaultColumnConstraint("1", c111);
	private static IColumnConstraint constraint2 = new NotNullColumnConstraint("", c112);

	@BeforeClass
	public static void init() throws Exception {
		schema1.addVertex(t1);
		schema2.addVertex(t1);
		schema2.addVertex(t2);
		schema3.addVertex(t1);
		schema3.addVertex(t2);
		schema4.addVertex(t1);
		schema4.addVertex(t2);
		schema5.addVertex(t1);
		schema5.addVertex(t2);
		schema6.addVertex(t1);
		schema7.addVertex(t1);
		schema9.addVertex(t2);
		schema11.addVertex(t1);
		schema12.addVertex(t1);
		schema13.addVertex(t1);
		schema31.addVertex(t1);
		schema31.addVertex(t2);

		schema1.addVertex(c1);
		schema2.addVertex(c1);
		schema2.addVertex(c12);
		schema2.addVertex(c2);
		schema2.addVertex(c22);
		schema3.addVertex(c1);
		schema3.addVertex(c12);
		schema3.addVertex(c2);
		schema3.addVertex(c22);
		schema4.addVertex(c1);
		schema4.addVertex(c2);
		schema5.addVertex(c2);
		schema5.addVertex(c3);
		schema6.addVertex(c2);
		schema9.addVertex(c1);
		schema11.addVertex(c111);
		schema12.addVertex(c112);
		schema13.addVertex(c113);
		schema31.addVertex(c1);
		schema31.addVertex(c12);
		schema31.addVertex(c2);
		schema31.addVertex(c22);

		schema1.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));

		schema2.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema2.addEdge(t1, c12, new TableHasColumnEdge(t1, c12));
		schema2.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		schema2.addEdge(t2, c22, new TableHasColumnEdge(t2, c22));

		schema3.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema3.addEdge(t1, c12, new TableHasColumnEdge(t1, c12));
		schema3.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		schema3.addEdge(t2, c22, new TableHasColumnEdge(t2, c22));

		schema4.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema4.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));

		schema5.addEdge(t2, c3, new TableHasColumnEdge(t2, c3));
		schema5.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));

		schema6.addEdge(t1, c2, new TableHasColumnEdge(t1, c2));

		schema9.addEdge(t2, c1, new TableHasColumnEdge(t2, c1));

		schema11.addEdge(t1, c111, new TableHasColumnEdge(t1, c111));

		schema12.addEdge(t1, c112, new TableHasColumnEdge(t1, c112));

		schema13.addEdge(t1, c113, new TableHasColumnEdge(t1, c113));

		List<IColumnConstraint> constraints11 = new ArrayList<IColumnConstraint>();
		List<IColumnConstraint> constraints12 = new ArrayList<IColumnConstraint>();

		constraints11.add(constraint1);

		constraints12.add(constraint1);
		constraints12.add(constraint2);

		((SqlColumnVertex) c111).setConstraints(constraints11);
		((SqlColumnVertex) c112).setConstraints(constraints12);

		schema31.addEdge(t1, c1, new TableHasColumnEdge(t1, c1));
		schema31.addEdge(t1, c12, new TableHasColumnEdge(t1, c12));
		schema31.addEdge(t2, c2, new TableHasColumnEdge(t2, c2));
		schema31.addEdge(t2, c22, new TableHasColumnEdge(t2, c22));
		schema31.addEdge(c12, c2, new ForeignKeyRelationEdge(c12, t2, c2));
	}

	@Before
	public void setUp() { }

	@Test
	public void isomorphicGraphsAreDetectedCorrectly() throws StructureGraphComparisonException  {
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema2, schema3);

		assertTrue(comparer.isIsomorphic());
	}

	@Test
	public void newTableIsDetectedCorrectly() throws StructureGraphComparisonException  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema5);
		Entry<ISqlElement, SchemaModification> entry = null;

		assertFalse(comparer1.isIsomorphic());
		assertNotNull(comparer1.comparisonResult);

		Iterator<Entry<ISqlElement, SchemaModification>> iter = comparer1.comparisonResult.getModifications().entrySet().iterator();

		while (entry == null && iter.hasNext()) {
			Entry<ISqlElement, SchemaModification> candidate = iter.next();

			if (candidate.getKey() instanceof SqlTableVertex) {
				entry = candidate;
			}
		}

		assertEquals(SchemaModification.CREATE_TABLE, entry.getValue());
		assertEquals(t2.getSqlElementId(), entry.getKey().getSqlElementId());
	}

	@Test
	public void removedTableIsDetectedCorrectly() throws StructureGraphComparisonException  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema8);

		assertFalse(comparer1.isIsomorphic());
		assertNotNull(comparer1.comparisonResult);
		assertTrue(comparer1.comparisonResult.getModifications().size() > 0);

		for (Entry<ISqlElement, SchemaModification> entry : comparer1.comparisonResult.getModifications().entrySet()) {
			if (entry.getValue() == SchemaModification.DELETE_TABLE) {
				assertEquals(t1.getSqlElementId(), entry.getKey().getSqlElementId());
				break;
			}
		}
	}

	@Test
	public void renamedTableIsDetectedCorrectly() throws StructureGraphComparisonException  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema9);
		Entry<ISqlElement, SchemaModification> renameEntry = null;

		assertFalse(comparer1.isIsomorphic());
		assertNotNull(comparer1.comparisonResult);

		for (Entry<ISqlElement, SchemaModification> entry : comparer1.comparisonResult.getModifications().entrySet()) {
			if (entry.getValue() == SchemaModification.RENAME_TABLE) {
				renameEntry = entry;
				break;
			}
		}

		assertNotNull(renameEntry);
		assertEquals(t2.getSqlElementId(), renameEntry.getKey().getSqlElementId());
	}

	@Test
	public void newColumnIsDetectedCorrectly() throws StructureGraphComparisonException  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema7, schema1);

		assertFalse(comparer1.isIsomorphic());
		assertNotNull(comparer1.comparisonResult);

		Entry<ISqlElement, SchemaModification> entry = comparer1.comparisonResult.getModifications().entrySet().iterator().next();

		assertEquals(SchemaModification.CREATE_COLUMN, entry.getValue());
		assertEquals(c1.getSqlElementId(), entry.getKey().getSqlElementId());
	}

	@Test
	public void removedColumnIsDetectedCorrectly() throws StructureGraphComparisonException  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema7);

		assertFalse(comparer1.isIsomorphic());
		assertNotNull(comparer1.comparisonResult);

		Entry<ISqlElement, SchemaModification> entry = comparer1.comparisonResult.getModifications().entrySet().iterator().next();

		assertEquals(SchemaModification.DELETE_COLUMN, entry.getValue());
		assertEquals(c1.getSqlElementId(), entry.getKey().getSqlElementId());
	}

	@Test
	public void renamedColumnIsDetectedCorrectly() throws StructureGraphComparisonException  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema1, schema6);

		assertFalse(comparer1.isIsomorphic());
		assertNotNull(comparer1.comparisonResult);

		Entry<ISqlElement, SchemaModification> entry = comparer1.comparisonResult.getModifications().entrySet().iterator().next();

		assertEquals(SchemaModification.RENAME_COLUMN, entry.getValue());
		assertEquals(c2.getSqlElementId(), entry.getKey().getSqlElementId());
	}

	@Test
	public void movedColumnIsDetectedCorrectly() throws StructureGraphComparisonException  {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema4, schema5);

		assertFalse(comparer1.isIsomorphic());
		assertNotNull(comparer1.comparisonResult);

		Entry<ISqlElement, SchemaModification> entry = comparer1.comparisonResult.getModifications().entrySet().iterator().next();

		assertEquals(SchemaModification.MOVE_COLUMN, entry.getValue());
		assertEquals(c1.getSqlElementId(), entry.getKey().getSqlElementId());
	}

	@Test
	public void unchangedColumnAttributesDetectedCorrectly() throws StructureGraphComparisonException {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema11, schema11);
		SqlSchemaColumnComparisonResult.ColumnConstraintComparisonResult cccr = comparer1.comparisonResult.getColumnComparisonResults().get(c111).getConstraintComparisonResult();

		assertNotNull(comparer1.comparisonResult);
		assertEquals(1, comparer1.comparisonResult.getColumnComparisonResults().size());
		assertFalse(comparer1.comparisonResult.getColumnComparisonResults().get(c111).hasColumnTypeChanged());
		assertEquals(0, cccr.getAddedConstraints().size());
		assertEquals(0, cccr.getRemovedConstraints().size());
	}

	@Test
	public void changedColumnTypeDetectedCorrectly() throws StructureGraphComparisonException {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema11, schema12);

		assertTrue(comparer1.comparisonResult.getColumnComparisonResults().get(c112).hasColumnTypeChanged());
	}

	@Test
	public void addedColumnConstraintDetectedCorrectly() throws StructureGraphComparisonException {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema11, schema12);
		SqlSchemaColumnComparisonResult.ColumnConstraintComparisonResult cccr = comparer1.comparisonResult.getColumnComparisonResults().get(c112).getConstraintComparisonResult();

		assertTrue(cccr.getAddedConstraints().contains(constraint2));
	}

	@Test
	public void removedColumnConstraintDetectedCorrectly() throws StructureGraphComparisonException {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema11, schema13);
		SqlSchemaColumnComparisonResult.ColumnConstraintComparisonResult cccr = comparer1.comparisonResult.getColumnComparisonResults().get(c113).getConstraintComparisonResult();

		assertTrue(cccr.getRemovedConstraints().contains(constraint1));
	}

	@Test
	public void noChangeInForeignKeyRelationsDetectedCorrectly() throws StructureGraphComparisonException {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema31, schema31);
		SqlSchemaComparisonResult result = comparer1.comparisonResult;

		assertNotNull(result);
		assertEquals(0, result.getAddedForeignKeyRelations().size());
		assertEquals(0, result.getRemovedForeignKeyRelations().size());
	}

	@Test
	public void addedForeignKeyRelationDetectedCorrectly() throws StructureGraphComparisonException {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema3, schema31);
		SqlSchemaComparisonResult result = comparer1.comparisonResult;

		assertNotNull(result);
		assertEquals(1, result.getAddedForeignKeyRelations().size());
		assertEquals(0, result.getRemovedForeignKeyRelations().size());
	}

	@Test
	public void removedForeignKeyRelationDetectedCorrectly() throws StructureGraphComparisonException {
		SqlSchemaComparer comparer1 = new SqlSchemaComparer(schema31, schema3);
		SqlSchemaComparisonResult result = comparer1.comparisonResult;

		assertNotNull(result);
		assertEquals(0, result.getAddedForeignKeyRelations().size());
		assertEquals(1, result.getRemovedForeignKeyRelations().size());
	}

	@After
	public void tearDown() {

	}
}
