package org.iti.sqlSchemaComparison.frontends;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import org.gibello.zql.ParseException;
import org.gibello.zql.ZFromItem;
import org.gibello.zql.ZQuery;
import org.gibello.zql.ZSelectItem;
import org.gibello.zql.ZStatement;
import org.gibello.zql.ZqlParser;
import org.iti.sqlSchemaComparison.edge.TableHasColumnEdge;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class SqlStatementFrontend implements ISqlSchemaFrontend {

	private String statement;
	
	public String getStatement() {
		return statement;
	}
	
	private Graph<ISqlElement, DefaultEdge> databaseSchema;

	public Graph<ISqlElement, DefaultEdge> getDatabaseSchema() {
		return databaseSchema;
	}

	@Override
	public Graph<ISqlElement, DefaultEdge> createSqlSchema() {
		Graph<ISqlElement, DefaultEdge> result = null;
		ZStatement statement = parseStatement();
		
		if (statement != null)
			result = createGraph(statement); 
		
		return result;
	}

	private ZStatement parseStatement() {		
		try {
			InputStream is = new ByteArrayInputStream(statement.getBytes("UTF-8"));
			ZqlParser parser = new ZqlParser(is);
			
			return parser.readStatement();
		} catch (UnsupportedEncodingException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Graph<ISqlElement, DefaultEdge> createGraph(ZStatement statement) {
		Graph<ISqlElement, DefaultEdge> result = null;
		
		if (statement instanceof ZQuery)
			result = createGraphFromQuery((ZQuery)statement);
			
		return result;
	}

	private Graph<ISqlElement, DefaultEdge> createGraphFromQuery(ZQuery query) {
		Graph<ISqlElement, DefaultEdge> schema = new SimpleGraph<ISqlElement, DefaultEdge>(DefaultEdge.class);

		for (Object item : query.getFrom()) {
			ZFromItem fromItem = (ZFromItem) item;
			
			createTableSchema(schema, query, fromItem);
		}
		
		return schema;
	}

	private void createTableSchema(Graph<ISqlElement, DefaultEdge> schema,
			ZQuery query, ZFromItem fromItem) {
		if (databaseSchema != null) {
			ISqlElement table = SqlElementFactory.getMatchingSqlElement(SqlElementType.Table, fromItem.getTable(), databaseSchema.vertexSet());
			
			if (table == null)
				throw new IllegalArgumentException(String.format("Table %s does not exist in schema!", fromItem.getTable()));
		}
		
		ISqlElement table = SqlElementFactory.createSqlElement(SqlElementType.Table, fromItem.getTable());
		schema.addVertex(table);
		
		for (Object item : query.getSelect()) {
			ZSelectItem selectItem = (ZSelectItem) item;
			
			if (query.getFrom().size() == 1 || columnMatchesTable(query, fromItem, selectItem)) {
				ISqlElement column = new SqlColumnVertex(selectItem.getColumn(), null, table.getSqlElementId());
				
				schema.addVertex(column);
				schema.addEdge(table, column, new TableHasColumnEdge(table, column));
			}
		}
	}

	private boolean columnMatchesTable(ZQuery query, ZFromItem fromItem,
			ZSelectItem selectItem) {
		String tableForColumn = selectItem.getTable();
		
		if (tableForColumn == null && databaseSchema == null)
			throw new IllegalArgumentException("Cannot resolve table for column because no database schema is passed!");
		
		if (tableForColumn == null) {
			Set<String> tables = getTablesFromQuery(query);
			Set<String> matchingTables = getTablesContainingColumn(tables, selectItem.getColumn());
			
			if (matchingTables.size() > 1)
				throw new IllegalArgumentException("Column " + selectItem.getColumn() + " is part of more than one table!");
			
			if (matchingTables.size() == 0)
				throw new IllegalArgumentException("No matching table for column " + selectItem.getColumn() + " exists!");
			
			return matchingTables.contains(fromItem.getTable());
			
		} else {
			Set<String> tables = new HashSet<>();
			
			if (fromItem.getAlias() != null && tableForColumn.equals(fromItem.getAlias()))
				tableForColumn = fromItem.getTable();
			
			tables.add(tableForColumn);
			
			Set<String> matchingTables = getTablesContainingColumn(tables, selectItem.getColumn());
			
			return matchingTables.contains(fromItem.getTable());
		}
	}

	private Set<String> getTablesFromQuery(ZQuery query) {
		Set<String> tables = new HashSet<>();
		
		for (Object item : query.getFrom())
			tables.add(((ZFromItem) item).getTable());
		
		return tables;
	}

	private Set<String> getTablesContainingColumn(Set<String> tables, String columnName) {
		Set<String> matchingTables = new HashSet<>();

		for (String tableName : tables) {
			ISqlElement table = SqlElementFactory.getMatchingSqlElement(SqlElementType.Table, tableName, databaseSchema.vertexSet());
			
			
			for (DefaultEdge e : databaseSchema.edgeSet()) {
				if (e instanceof TableHasColumnEdge) {
					TableHasColumnEdge edge = (TableHasColumnEdge) e;
					
					if (edge.getTable().equals(table) && edge.getColumn().getSqlElementId().equals(columnName))
						matchingTables.add(table.getSqlElementId());
				}
			}
		}
		
		return matchingTables;
	}

	public SqlStatementFrontend(String statement, Graph<ISqlElement, DefaultEdge> databaseSchema) {
		if (statement == null || statement == "")
			throw new NullPointerException("SQL statement must not be null or empty!");
		
		this.statement = statement;
		this.databaseSchema = databaseSchema;
	}

}
