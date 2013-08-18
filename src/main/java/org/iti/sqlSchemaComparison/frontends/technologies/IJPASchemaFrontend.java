package org.iti.sqlSchemaComparison.frontends.technologies;

import org.iti.sqlSchemaComparison.frontends.ISqlSchemaFrontend;

public interface IJPASchemaFrontend extends ISqlSchemaFrontend {
	final static String GETTER_PREFIX = "get";
	final static String ENTITY = "Entity";
	final static String TABLE = "Table";
	final static String COLUMN = "Column";
	final static String TABLE_NAME = "name";
}
