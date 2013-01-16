package org.iti.sqlSchemaComparison.vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;

public abstract class SqlElementFactory {

	public static ISqlElement createSqlElement(SqlElementType type, String id) {
		switch (type) {
			case Column:
				return new SqlColumnVertex(id, "", new ArrayList<IColumnConstraint>());
				
			case Type:
				return new SqlTypeVertex(id);
		
			default:
				return new SqlTableVertex(id);
		}
	}

	public static boolean equals(ISqlElement t, Object o) {
		if (o instanceof ISqlElement) {
			ISqlElement e = (ISqlElement)o;
			
			return t.getSqlElementId().equals(e.getSqlElementId())
					&& t.getSqlElementType().equals(e.getSqlElementType());
		}
		
		return false;
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
}
