package org.iti.sqlSchemaComparison.frontends.database;

import java.util.Map.Entry;

import org.iti.sqlSchemaComparison.SchemaModification;
import org.iti.sqlSchemaComparison.SqlSchemaComparisonResult;
import org.iti.sqlSchemaComparison.vertex.ISqlElement;

class DatabaseFrontendTestHelper {

	public static Entry<ISqlElement, SchemaModification> getModificationOfType(SqlSchemaComparisonResult result, SchemaModification schemaModification) {
		for (Entry<ISqlElement, SchemaModification> e : result.getModifications().entrySet()) {
			if (e.getValue().equals(schemaModification)) {
				return e;
			}
		}

		return null;
	}

}
