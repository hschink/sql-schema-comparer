package org.iti.sqlSchemaComparison.edge;

import org.iti.sqlSchemaComparison.vertex.SqlColumnVertex;
import org.iti.sqlSchemaComparison.vertex.SqlTableVertex;

public interface ITableHasColumnEdge {

	SqlTableVertex getTable();
	
	SqlColumnVertex getColumn();
}
