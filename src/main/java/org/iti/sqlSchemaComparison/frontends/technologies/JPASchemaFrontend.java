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

package org.iti.sqlSchemaComparison.frontends.technologies;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iti.sqlSchemaComparison.edge.ColumnHasConstraint;
import org.iti.sqlSchemaComparison.edge.ForeignKeyRelationEdge;
import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.ColumnConstraintVertex;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint.ConstraintType;
import org.iti.structureGraph.nodes.IStructureElement;
import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class JPASchemaFrontend implements IJPASchemaFrontend {

	private String filePath;

	private static class JPAAnnotationVisitor extends VoidVisitorAdapter<DirectedGraph<IStructureElement, DefaultEdge>> {

		public Map<String, String> classToTable = new HashMap<>();
		public Map<String, ClassOrInterfaceDeclaration> classDeclarations = new HashMap<>();

		private final static String TRANSIENT = "Transient";
		private final static String ID = "Id";

		private DirectedGraph<IStructureElement, DefaultEdge> schema;

		private ISqlElement lastVisitedClass;

		public JPAAnnotationVisitor(DirectedGraph<IStructureElement, DefaultEdge> schema) {
			this.schema = schema;
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, DirectedGraph<IStructureElement, DefaultEdge> arg) {

			if (n.getAnnotations() != null && isAnnotationAvailable(n.getAnnotations(), ENTITY)) {
				processClass(n);
			}

			super.visit(n, arg);
		}

		private void processClass(ClassOrInterfaceDeclaration n) {
			String tableName = getTableName(n);
			ISqlElement table = SqlElementFactory.createSqlElement(SqlElementType.Table, tableName);

			table.setSourceElement(n);

			schema.addVertex(table);

			lastVisitedClass = table;

			classToTable.put(n.getName().toString(), tableName);
			classDeclarations.put(n.getName(), n);
		}

		@Override
		public void visit(MethodDeclaration n, DirectedGraph<IStructureElement, DefaultEdge> arg) {

			if (isGetter(n) && (n.getAnnotations() == null || !isAnnotationAvailable(n.getAnnotations(), TRANSIENT))) {
				processMethod(n);
			}

			super.visit(n, arg);
		}

		private boolean isGetter(MethodDeclaration n) {
			return n.getName().startsWith(GETTER_PREFIX);
		}

		private void processMethod(MethodDeclaration n) {
			String columnName = getColumnName(n);
			ISqlElement column = new SqlColumnVertex(columnName, lastVisitedClass.getName());

			schema.addVertex(column);
			schema.addEdge(lastVisitedClass, column, new TableHasColumnEdge(lastVisitedClass, column));

			column.setSourceElement(n);

			if (isAnnotationAvailable(n.getAnnotations(), ID)) {
				ISqlElement columnConstraint = new ColumnConstraintVertex(columnName, ConstraintType.PRIMARY_KEY);

				schema.addVertex(columnConstraint);
				schema.addEdge(column, columnConstraint, new ColumnHasConstraint());
			}
		}

	}

	private static class PrimaryKeyVisitor extends VoidVisitorAdapter<DirectedGraph<IStructureElement, DefaultEdge>> {

		private DirectedGraph<IStructureElement, DefaultEdge> schema;

		private Map<String, String> classToTable;

		private Map<String, ClassOrInterfaceDeclaration> classDeclarations = new HashMap<>();

		public PrimaryKeyVisitor(DirectedGraph<IStructureElement, DefaultEdge> schema,
				Map<String, String> classToTable,
				Map<String, ClassOrInterfaceDeclaration> classDeclarations) {
			this.schema = schema;
			this.classToTable = classToTable;
			this.classDeclarations = classDeclarations;
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, DirectedGraph<IStructureElement, DefaultEdge> arg) {

			if (n.getAnnotations() != null && isAnnotationAvailable(n.getAnnotations(), ENTITY)) {
				processClass(n);
			}

			super.visit(n, arg);
		}

		private void processClass(ClassOrInterfaceDeclaration n) {
			ISqlElement primaryKeyColumn = getPrimaryKeyOfType(n);

			if (primaryKeyColumn == null) {
				primaryKeyColumn = getPrimaryKeyOfSupertypes(n.getExtends());

				setPrimaryKeyOfTable(n, primaryKeyColumn);
			}
		}

		private void setPrimaryKeyOfTable(ClassOrInterfaceDeclaration n, ISqlElement primaryKeyColumn) {
			if (primaryKeyColumn != null && primaryKeyColumn instanceof SqlColumnVertex) {
				String tableId = classToTable.get(n.getName().toString());
				ISqlElement table = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, tableId, schema.vertexSet());
				SqlColumnVertex foreignKeyColumn = (SqlColumnVertex)primaryKeyColumn;
				ISqlElement foreignKeyTable = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, foreignKeyColumn.getTable(), schema.vertexSet());
				String columnName = foreignKeyColumn.getName();
				ISqlElement column = new SqlColumnVertex(columnName, table.getName());

				schema.addVertex(column);
				schema.addEdge(table, column, new TableHasColumnEdge(table, column));
				schema.addEdge(table, column, new ForeignKeyRelationEdge(column, foreignKeyTable, foreignKeyColumn));

				column.setSourceElement(n);

				ISqlElement columnConstraint = new ColumnConstraintVertex(columnName, ConstraintType.PRIMARY_KEY);

				schema.addVertex(columnConstraint);
				schema.addEdge(column, columnConstraint, new ColumnHasConstraint());
			}
		}

		private ISqlElement getPrimaryKeyOfSupertypes(
				List<ClassOrInterfaceType> supertypes) {

			ISqlElement primaryKeyColumn = null;

			for (ClassOrInterfaceType supertype : supertypes) {
				ClassOrInterfaceDeclaration superclass = classDeclarations.get(supertype.getName());

				if (superclass != null) {
					primaryKeyColumn = getPrimaryKeyOfType(superclass);

					if (primaryKeyColumn != null)
						break;

					primaryKeyColumn = getPrimaryKeyOfSupertypes(superclass.getExtends());
				}
			}

			return primaryKeyColumn;
		}

		private ISqlElement getPrimaryKeyOfType(ClassOrInterfaceDeclaration type) {
			String tableId = classToTable.get(type.getName().toString());
			ISqlElement table = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, tableId, schema.vertexSet());

			return SqlElementFactory.getPrimaryKey(table, schema);
		}

	}

	private static class ForeignKeyVisitor extends VoidVisitorAdapter<Graph<ISqlElement, DefaultEdge>> {

		private final static String[] RELATIONSHIP_ANNOTATIONS = new String[]
		{
			"@ManyToMany",
			"ManyToOne",
			"OneToMany",
			"OneToOne"
		};

		private DirectedGraph<IStructureElement, DefaultEdge> schema;

		private Map<String, String> classToTable = new HashMap<>();

		private ISqlElement lastVisitedClass;

		public ForeignKeyVisitor(DirectedGraph<IStructureElement, DefaultEdge> schema, Map<String, String> classToTable) {
			this.schema = schema;
			this.classToTable = classToTable;
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Graph<ISqlElement, DefaultEdge> arg) {

			if (n.getAnnotations() != null && isAnnotationAvailable(n.getAnnotations(), ENTITY)) {
				processClass(n);
			}

			super.visit(n, arg);
		}

		private void processClass(ClassOrInterfaceDeclaration n) {
			String id = getTableName(n);

			lastVisitedClass = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, id, schema.vertexSet());
		}

		@Override
		public void visit(MethodDeclaration n, Graph<ISqlElement, DefaultEdge> arg) {

			if (isGetter(n) && (n.getAnnotations() != null && isAnnotationAvailable(n.getAnnotations(), RELATIONSHIP_ANNOTATIONS))) {
				processMethod(n);
			}

			super.visit(n, arg);
		}

		private boolean isGetter(MethodDeclaration n) {
			return n.getName().startsWith(GETTER_PREFIX);
		}

		private void processMethod(MethodDeclaration n) {
			String columnId = lastVisitedClass.getName() + "." + getColumnName(n);
			String foreignTableId = classToTable.get(n.getType().toString());
			ISqlElement foreignKeyTable = SqlElementFactory.getMatchingSqlElement(SqlTableVertex.class, foreignTableId, schema.vertexSet());
			ISqlElement referencingColumn = SqlElementFactory.getMatchingSqlColumns(columnId, schema.vertexSet(), true).get(0);
			ISqlElement foreignKeyColumn = SqlElementFactory.getPrimaryKey(foreignKeyTable, schema);

			if (referencingColumn != null && foreignKeyColumn != null) {
				schema.addEdge(referencingColumn, foreignKeyColumn, new ForeignKeyRelationEdge(referencingColumn, foreignKeyTable, foreignKeyColumn));
			}
		}

	}

	private static boolean isAnnotationAvailable(
		List<AnnotationExpr> annotations, String annotation) {

		return getAnnotation(annotations, annotation) != null;
	}

	private static boolean isAnnotationAvailable(
		List<AnnotationExpr> annotations, String[] relationshipAnnotations) {

		for (AnnotationExpr annotation : annotations)
			if (Arrays.asList(relationshipAnnotations).contains(annotation.getName().toString()))
				return true;

		return false;
	}

	private static String getTableName(ClassOrInterfaceDeclaration n) {
		AnnotationExpr tableAnnotation = getAnnotation(n.getAnnotations(), TABLE);

		if (tableAnnotation != null && tableAnnotation instanceof NormalAnnotationExpr) {
			NormalAnnotationExpr a = (NormalAnnotationExpr)tableAnnotation;

			for (MemberValuePair p : a.getPairs()) {
				if (p.getName().equals(TABLE_NAME))
					return p.getValue().toString().replace("\"", "");
			}
		}

		return n.getName();
	}

	private static String getColumnName(MethodDeclaration n) {
		return getColumnName(n, COLUMN);
	}

	private static String getColumnName(MethodDeclaration n, String annotation) {
		AnnotationExpr tableAnnotation = getAnnotation(n.getAnnotations(), annotation);

		if (tableAnnotation != null && tableAnnotation instanceof NormalAnnotationExpr) {
			NormalAnnotationExpr a = (NormalAnnotationExpr)tableAnnotation;

			for (MemberValuePair p : a.getPairs()) {
				if (p.getName().equals(TABLE_NAME))
					return p.getValue().toString();
			}
		}

		return n.getName().substring(GETTER_PREFIX.length(), n.getName().length()).toLowerCase();
	}

	private static AnnotationExpr getAnnotation(List<AnnotationExpr> annotations, String annotation) {
		if (annotations != null) {
			for (AnnotationExpr expr : annotations) {
				if (expr.getName().toString().equals(annotation))
					return expr;
			}
		}

		return null;
	}

	@Override
	public DirectedGraph<IStructureElement, DefaultEdge> createSqlSchema() {
		DirectedGraph<IStructureElement, DefaultEdge> schema = null;

		try {
			schema = tryCreateSqlSchema();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return schema;
	}

	private static FilenameFilter javaFilenameFilter = new FilenameFilter() {

		@Override
		public boolean accept(File arg0, String arg1) {
			return arg1.endsWith(".java");
		}
	};

	private DirectedGraph<IStructureElement, DefaultEdge> tryCreateSqlSchema() throws ParseException, IOException {
		DirectedGraph<IStructureElement, DefaultEdge> schema = new SimpleDirectedGraph<IStructureElement, DefaultEdge>(DefaultEdge.class);
		File file = new File(filePath);
		List<CompilationUnit> cus = new ArrayList<>();
		Map<String, String> classToTable = new HashMap<>();
		Map<String, ClassOrInterfaceDeclaration> classDeclarations = new HashMap<>();

		if (file.isDirectory()) {
			for (String f : file.list(javaFilenameFilter)) {
				File child = new File(file, f);

				CompilationUnit cu = getCompilationUnit(child.getAbsolutePath());

				if (cu != null)
					cus.add(cu);
			}
		} else {
			CompilationUnit cu = getCompilationUnit(filePath);

			if (cu != null)
				cus.add(cu);
		}

		for (CompilationUnit c : cus) {
			parseJavaCompilationUnit(c, schema, classToTable, classDeclarations);
		}

		for (CompilationUnit c : cus) {
			createForeignKeyPrimaryRelationships(c, schema, classToTable, classDeclarations);
		}

		for (CompilationUnit c : cus) {
			createForeignKeyRelationships(c, schema, classToTable);
		}

        return schema;
	}

	private CompilationUnit getCompilationUnit(String filePath)
			throws FileNotFoundException, ParseException, IOException {
		FileInputStream in = new FileInputStream(filePath);

        CompilationUnit cu;

        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }

        return cu;
	}

	private void parseJavaCompilationUnit(CompilationUnit cu,
			DirectedGraph<IStructureElement, DefaultEdge> schema,
			Map<String, String> classToTable,
			Map<String, ClassOrInterfaceDeclaration> classDeclarations) {
		JPAAnnotationVisitor visitor = new JPAAnnotationVisitor(schema);
		visitor.visit(cu, null);

		classToTable.putAll(visitor.classToTable);
		classDeclarations.putAll(visitor.classDeclarations);
	}

	private void createForeignKeyPrimaryRelationships(CompilationUnit cu,
			DirectedGraph<IStructureElement, DefaultEdge> schema,
			Map<String, String> classToTable,
			Map<String, ClassOrInterfaceDeclaration> classDeclarations) {
		PrimaryKeyVisitor visitor = new PrimaryKeyVisitor(schema, classToTable, classDeclarations);
		visitor.visit(cu, null);
	}

	private void createForeignKeyRelationships(CompilationUnit cu,
			DirectedGraph<IStructureElement, DefaultEdge> schema,
			Map<String, String> classToTable) {

		ForeignKeyVisitor visitor = new ForeignKeyVisitor(schema, classToTable);
		visitor.visit(cu, null);
	}

	public JPASchemaFrontend(String filePath) {
		if (filePath == null || filePath == "")
			throw new NullPointerException("Path to JPA file(s) must not be null or empty!");

		this.filePath = filePath;
	}

}
