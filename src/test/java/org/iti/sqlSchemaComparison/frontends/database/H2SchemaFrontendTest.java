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

package org.iti.sqlSchemaComparison.frontends.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.iti.sqlSchemaComparison.SchemaModification;
import org.iti.sqlSchemaComparison.SqlSchemaColumnComparisonResult;
import org.iti.sqlSchemaComparison.SqlSchemaComparer;
import org.iti.sqlSchemaComparison.SqlSchemaComparisonResult;
import org.iti.sqlSchemaComparison.edge.ForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.PrimaryKeyColumnConstraint;
import org.iti.structureGraph.comparison.StructureGraphComparisonException;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class H2SchemaFrontendTest {

	public static final String DATABASE_FILE_PATH = ".//src//test//java//databases//hrm";
	
	public static final String DROPPED_COLUMN_DATABASE_FILE_PATH = ".//src//test//java//databases//refactored//hrm_DropColumn";
	public static final String DROPPED_TABLE_DATABASE_FILE_PATH = ".//src//test//java//databases//refactored//hrm_DropTable";
	public static final String MOVE_COLUMN_DATABASE_FILE_PATH = ".//src//test//java//databases//refactored//hrm_MoveColumn";
	public static final String RENAME_COLUMN_DATABASE_FILE_PATH = ".//src//test//java//databases//refactored//hrm_RenameColumn";
	public static final String RENAME_TABLE_DATABASE_FILE_PATH = ".//src//test//java//databases//refactored//hrm_RenameTable";
	public static final String REPLACE_COLUMN_DATABASE_FILE_PATH = ".//src//test//java//databases//refactored//hrm_ReplaceColumn";
	public static final String REPLACE_LOB_WITH_TABLE_DATABASE_FILE_PATH = ".//src//test//java//databases//refactored//hrm_ReplaceLobWithTable";
	
	private static final String DROPPED_COLUMN_NAME = "BOSS";
	private static final String DROPPED_TABLE_NAME = "EXTERNAL_STAFF";
	private static final String MOVE_COLUMN_NAME = "ACCOUNT";
	private static final String RENAME_COLUMN_NAME = "PHONE";
	private static final String RENAME_TABLE_NAME = "EXTERNAL_EMPLOYEES";
	private static final String REPLACE_COLUMN_NAME = "COMPANY";
	private static final String REPLACE_COLUMN_TYPE = "CLOB";
	private static final String REPLACE_LOB_WITH_TABLE = "CUSTOMER_ADDRESS";
	private static final String REPLACE_LOB_WITH_COLUMN = "ADDRESS";
	
	@Before
	public void setUp() { }
	
	@Test
	public void databaseConnectionEstablishedCorrectly() {
		ISqlSchemaFrontend frontend = new H2SchemaFrontend(DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema = frontend.createSqlSchema();
		
		assertNotNull(schema);
		assertEquals(7, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema.vertexSet()).size());
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()).size());
		assertEquals(7, getColumnWithConstraint(SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema.vertexSet()), PrimaryKeyColumnConstraint.class).size());
		
	}
	
	private List<ISqlElement> getColumnWithConstraint(Set<ISqlElement> sqlElementsOfType, Class<?> constraintType) {
		List<ISqlElement> columns = new ArrayList<>();
		
		for (ISqlElement e : sqlElementsOfType) {
			for (IColumnConstraint c : ((SqlColumnVertex) e).getConstraints()) {
				if (constraintType.isAssignableFrom(c.getClass())) {
					columns.add(e);
					break;
				}
			}
		}
		
		return columns;
	}
	
	@Test
	public void foreignKeysEstablishedCorrectly() {
		ISqlSchemaFrontend frontend = new H2SchemaFrontend(DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema = frontend.createSqlSchema();
		int foreignKeyEdges = 0;
		
		for (DefaultEdge edge : schema.edgeSet())
			if (edge instanceof ForeignKeyRelationEdge)
				foreignKeyEdges++;
		
		
		assertEquals(7, foreignKeyEdges);
		
	}

	@Test
	public void tableHasColumnRelationsEstablishedCorrectly() {
		ISqlSchemaFrontend frontend = new H2SchemaFrontend(DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema = frontend.createSqlSchema();
		int tableHasColumnEdges = 0;
		
		for (DefaultEdge edge : schema.edgeSet())
			if (edge instanceof TableHasColumnEdge)
				tableHasColumnEdges++;
		
		
		assertEquals(29, tableHasColumnEdges);
		
	}
	
	@Test
	public void droppedColumnDetectedCorrectly() throws StructureGraphComparisonException {
		ISqlSchemaFrontend frontend1 = new H2SchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new H2SchemaFrontend(DROPPED_COLUMN_DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		DirectedGraph<IStructureElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;
		
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet()).size());
		assertEquals(28, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet()).size());
		
		ISqlElement elements = null;
		
		for (Entry<ISqlElement, SchemaModification> entry : result.getModifications().entrySet()) {
			elements = entry.getKey();
		}

		assertEquals(DROPPED_COLUMN_NAME, elements.getSqlElementId());
	}
	
	@Test
	public void droppedTableDetectedCorrectly() throws StructureGraphComparisonException {
		ISqlSchemaFrontend frontend1 = new H2SchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new H2SchemaFrontend(DROPPED_TABLE_DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		DirectedGraph<IStructureElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;
		
		assertEquals(7, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema1.vertexSet()).size());
		assertEquals(6, SqlElementFactory.getSqlElementsOfType(SqlElementType.Table, schema2.vertexSet()).size());

		for (Entry<ISqlElement, SchemaModification> entry : result.getModifications().entrySet()) {
			if (entry.getValue() == SchemaModification.DELETE_TABLE) {
				assertEquals(SchemaModification.DELETE_TABLE, entry.getValue());
				assertEquals(DROPPED_TABLE_NAME, entry.getKey().getSqlElementId());
			}
		}
	}
	
	@Test
	public void moveColumnDetectedCorrectly() throws StructureGraphComparisonException {
		ISqlSchemaFrontend frontend1 = new H2SchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new H2SchemaFrontend(MOVE_COLUMN_DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		DirectedGraph<IStructureElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;
		
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet()).size());
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet()).size());

		Entry<ISqlElement, SchemaModification> entry = result.getModifications().entrySet().iterator().next();

		assertEquals(SchemaModification.MOVE_COLUMN, entry.getValue());
		assertEquals(MOVE_COLUMN_NAME, entry.getKey().getSqlElementId());
	}
	
	@Test
	public void renameColumnDetectedCorrectly() throws StructureGraphComparisonException {
		ISqlSchemaFrontend frontend1 = new H2SchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new H2SchemaFrontend(RENAME_COLUMN_DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		DirectedGraph<IStructureElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;
		
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet()).size());
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet()).size());

		Entry<ISqlElement, SchemaModification> entry = result.getModifications().entrySet().iterator().next();

		assertEquals(SchemaModification.RENAME_COLUMN, entry.getValue());
		assertEquals(RENAME_COLUMN_NAME, entry.getKey().getSqlElementId());
	}
	
	@Test
	public void renameTableDetectedCorrectly() throws StructureGraphComparisonException {
		ISqlSchemaFrontend frontend1 = new H2SchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new H2SchemaFrontend(RENAME_TABLE_DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		DirectedGraph<IStructureElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;
		
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet()).size());
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet()).size());

		for (Entry<ISqlElement, SchemaModification> entry : result.getModifications().entrySet()) {
			if (entry.getValue() == SchemaModification.RENAME_TABLE) {
				assertEquals(RENAME_TABLE_NAME, entry.getKey().getSqlElementId());
			}
		}
	}
	
	@Test
	public void replaceColumnDetectedCorrectly() throws StructureGraphComparisonException {
		ISqlSchemaFrontend frontend1 = new H2SchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new H2SchemaFrontend(REPLACE_COLUMN_DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		DirectedGraph<IStructureElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;
		SqlSchemaColumnComparisonResult column = null;
		
		for (ISqlElement element : result.getColumnComparisonResults().keySet()) {
			if (element.getSqlElementId().equals(REPLACE_COLUMN_NAME)) {
				column = result.getColumnComparisonResults().get(element);
				break;
			}
		}
		
		assertNotNull(column);

		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet()).size());
		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet()).size());

		Entry<ISqlElement, SchemaModification> entry = result.getModifications().entrySet().iterator().next();

		assertEquals(SchemaModification.RENAME_COLUMN, entry.getValue());
		assertEquals(REPLACE_COLUMN_NAME, entry.getKey().getSqlElementId());
		assertTrue(column.hasColumnTypeChanged());
		assertEquals(REPLACE_COLUMN_TYPE, column.getCurrentColumnType());
	}
	
	@Test
	public void replaceLobWithTable() throws StructureGraphComparisonException {
		ISqlSchemaFrontend frontend1 = new H2SchemaFrontend(DATABASE_FILE_PATH);
		ISqlSchemaFrontend frontend2 = new H2SchemaFrontend(REPLACE_LOB_WITH_TABLE_DATABASE_FILE_PATH);
		DirectedGraph<IStructureElement, DefaultEdge> schema1 = frontend1.createSqlSchema();
		DirectedGraph<IStructureElement, DefaultEdge> schema2 = frontend2.createSqlSchema();
		SqlSchemaComparer comparer = new SqlSchemaComparer(schema1, schema2);
		SqlSchemaComparisonResult result = comparer.comparisonResult;

		assertEquals(29, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema1.vertexSet()).size());
		assertEquals(33, SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, schema2.vertexSet()).size());

		for (Entry<ISqlElement, SchemaModification> entry : result.getModifications().entrySet()) {
			if (entry.getValue() == SchemaModification.CREATE_TABLE) {
				assertEquals(REPLACE_LOB_WITH_TABLE, entry.getKey().getSqlElementId());
			}

			if (entry.getValue() == SchemaModification.DELETE_COLUMN) {
				assertEquals(REPLACE_LOB_WITH_COLUMN, entry.getKey().getSqlElementId());
			}
		}
	}

	@Test(expected=InvalidPathException.class)
	public void throwsInvalidFilePathExceptionForEmptyString() {
		ISqlSchemaFrontend frontend = new H2SchemaFrontend("");

		frontend.createSqlSchema();
	}

	@Test(expected=InvalidPathException.class)
	public void throwsInvalidFilePathExceptionForNull() {
		ISqlSchemaFrontend frontend = new H2SchemaFrontend(null);

		frontend.createSqlSchema();
	}

	@Test(expected=IllegalArgumentException.class)
	public void throwsInvalidArgumentExceptionOnInvalidFilePath() {
		ISqlSchemaFrontend frontend = new H2SchemaFrontend("dadidadam");

		frontend.createSqlSchema();
	}

	@After
	public void tearDown() { }
}
