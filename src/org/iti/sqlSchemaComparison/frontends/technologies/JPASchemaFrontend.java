package org.iti.sqlSchemaComparison.frontends.technologies;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class JPASchemaFrontend implements ISqlSchemaFrontend {

	private String filePath;
	
	private static class JPAAnnotationVisitor extends VoidVisitorAdapter<Graph<ISqlElement, DefaultEdge>> {

		private final static String ENTITY = "Entity";
		private final static String TABLE = "Table";
		private final static String TABLE_NAME = "name";
		private final static String COLUMN = "Column";
		private final static String TRANSIENT = "Transient";
		
		private final static String GETTER_PREFIX = "get";
		
		private Graph<ISqlElement, DefaultEdge> schema = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);
		
		public Graph<ISqlElement, DefaultEdge> getSchema() {
			return schema;
		}

		private ISqlElement lastVisitedClass;
		
		@Override
		public void visit(ClassOrInterfaceDeclaration n, Graph<ISqlElement, DefaultEdge> arg) {

			if (n.getAnnotations() != null && isAnnotationAvailable(n.getAnnotations(), ENTITY)) {
				processClass(n);
			}
			
			super.visit(n, arg);
		}

		private void processClass(ClassOrInterfaceDeclaration n) {
			String tableName = getTableName(n);
			ISqlElement table = SqlElementFactory.createSqlElement(SqlElementType.Table, tableName);
			schema.addVertex(table);
			
			lastVisitedClass = table;
		}

		private String getTableName(ClassOrInterfaceDeclaration n) {
			AnnotationExpr tableAnnotation = getAnnotation(n.getAnnotations(), TABLE);
			
			if (tableAnnotation != null && tableAnnotation instanceof NormalAnnotationExpr) {
				NormalAnnotationExpr a = (NormalAnnotationExpr)tableAnnotation;
				
				for (MemberValuePair p : a.getPairs()) {
					if (p.getName().equals(TABLE_NAME))
						return p.getValue().toString();
				}
			}
			
			return n.getName();
		}

		@Override
		public void visit(MethodDeclaration n, Graph<ISqlElement, DefaultEdge> arg) {

			if (isGetter(n) && (n.getAnnotations() == null || !isAnnotationAvailable(n.getAnnotations(), TRANSIENT))) {
				processMethod(n);
			}
			
			super.visit(n, arg);
		}

		private boolean isGetter(MethodDeclaration n) {
			return n.getName().startsWith(GETTER_PREFIX);
		}

		private void processMethod(MethodDeclaration n) {
			String id = getColumnName(n);
			String type = "?";
			List<IColumnConstraint> constraints = new ArrayList<>();
			
			ISqlElement column = new SqlColumnVertex(id, type, lastVisitedClass.getSqlElementId());
			
			((SqlColumnVertex) column).setConstraints(constraints);
			
			schema.addVertex(column);
			schema.addEdge(lastVisitedClass, column, new TableHasColumnEdge(lastVisitedClass, column));
		}

		private String getColumnName(MethodDeclaration n) {
			AnnotationExpr tableAnnotation = getAnnotation(n.getAnnotations(), COLUMN);
			
			if (tableAnnotation != null && tableAnnotation instanceof NormalAnnotationExpr) {
				NormalAnnotationExpr a = (NormalAnnotationExpr)tableAnnotation;
				
				for (MemberValuePair p : a.getPairs()) {
					if (p.getName().equals(TABLE_NAME))
						return p.getValue().toString();
				}
			}
			
			return n.getName().replaceFirst(GETTER_PREFIX, "").toLowerCase();
		}

		private boolean isAnnotationAvailable(
				List<AnnotationExpr> annotations, String annotation) {

			return getAnnotation(annotations, annotation) != null;
		}
		
		private AnnotationExpr getAnnotation(List<AnnotationExpr> annotations, String annotation) {
			if (annotations != null) {
				for (AnnotationExpr expr : annotations) {
					if (expr.getName().toString().equals(annotation))
						return expr;
				}
			}
			
			return null;
		}
		
	}
	
	@Override
	public Graph<ISqlElement, DefaultEdge> createSqlSchema() {
		Graph<ISqlElement, DefaultEdge> schema = null;
		
		try {
			schema = tryCreateSqlSchema();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		return schema;
	}

	private Graph<ISqlElement, DefaultEdge> tryCreateSqlSchema() throws ParseException, IOException {
		Graph<ISqlElement, DefaultEdge> schema = null;
		FileInputStream in = new FileInputStream(filePath);

        CompilationUnit cu;
        
        try {
            // parse the file
            cu = JavaParser.parse(in);

            schema = parseJavaCompilationUnit(cu);
        } finally {
            in.close();
        }

        return schema;
	}

	private Graph<ISqlElement, DefaultEdge> parseJavaCompilationUnit(CompilationUnit cu) {
		JPAAnnotationVisitor visitor = new JPAAnnotationVisitor();
		
		visitor.visit(cu, null);
		
		return visitor.getSchema();
	}

	public JPASchemaFrontend(String filePath) {
		if (filePath == null || filePath == "")
			throw new NullPointerException("Path to JPA file(s) must not be null or empty!");
		
		this.filePath = filePath;
	}
	
}
