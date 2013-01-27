package org.iti.sqlSchemaComparison.vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SqlElementFactory {

	public static ISqlElement createSqlElement(SqlElementType type, String id) {
		switch (type) {
			case Column:
				return null;
		
			default:
				return new SqlTableVertex(id);
		}
	}

	public static boolean equals(ISqlElement t, Object o) {
		if (o instanceof ISqlElement) {
			ISqlElement e = (ISqlElement)o;
			
			return equals(t.getSqlElementId(), e.getSqlElementId(), t.getSqlElementType(), e.getSqlElementType());
		}
		
		return false;
	}

	public static boolean equals(SqlColumnVertex t, Object o) {
		if (o instanceof SqlColumnVertex) {
			SqlColumnVertex e = (SqlColumnVertex)o;
			
			return equals(t.getSqlElementId(), e.getSqlElementId(), t.getSqlElementType(), e.getSqlElementType())
					&& t.getTable().equals(e.getTable());
		}
		
		return false;
	}

	private static boolean equals(String id, String otherId, SqlElementType type, SqlElementType otherType) {
		return id.equals(otherId) && type.equals(otherType);
	}
	
	public static int hashCode(ISqlElement t) {
		return t.getSqlElementId().hashCode() + t.getSqlElementType().hashCode();
	}
	
	public static Set<ISqlElement> getSqlElementsOfType(SqlElementType type, Collection<ISqlElement> vertices) {
		Set<ISqlElement> verticesOfType = new HashSet<>();
		
		for (ISqlElement t : vertices) {
			if (t.getSqlElementType() == type)
				verticesOfType.add(t);
		}
		
		return verticesOfType;
	}

	public static ISqlElement getMatchingSqlElement(ISqlElement vertex, Set<ISqlElement> vertices) {

		for (ISqlElement v : vertices)
			if (v.equals(vertex))
				return v;
		
		return null;
	}
	
	public static ISqlElement getMatchingSqlElement(SqlElementType type, String id, Collection<ISqlElement> vertices) {
		
		for (ISqlElement v : vertices)
			if (v.getSqlElementType().equals(type)) {
				if (type == SqlElementType.Column) {
					String otherTable = ((SqlColumnVertex) v).getTable();
					String otherId = otherTable + "." + v.getSqlElementId();
					
					if (otherId.equals(id))
						return v;
					
					continue;
				} else if (v.getSqlElementId().equals(id))
					return v;
			}
		
		return null;
	}
	
	public static List<ISqlElement> getMatchingSqlColumns(String id, Collection<ISqlElement> vertices, boolean matchColumnTable) {
		Set<ISqlElement> columns = SqlElementFactory.getSqlElementsOfType(SqlElementType.Column, vertices);
		List<ISqlElement> matchingColumns = new ArrayList<>();
		
		for (ISqlElement column : columns) {
			String otherTable =  (matchColumnTable) ? ((SqlColumnVertex) column).getTable() + "." : "";
			String otherId = otherTable + column.getSqlElementId();
			
			if (otherId.equals(id))
				matchingColumns.add(column);
		}
		
		return matchingColumns;
	}

}
