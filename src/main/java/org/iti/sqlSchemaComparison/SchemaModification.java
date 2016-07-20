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

public enum SchemaModification {
	NO_MODIFICATION,

	CREATE_TABLE,
	DELETE_TABLE,
	RENAME_TABLE,
	DELETE_AFTER_RENAME_TABLE,

	CREATE_COLUMN,
	DELETE_COLUMN,
	RENAME_COLUMN,
	MOVE_COLUMN,

	CREATE_CONSTRAINT,
	DELETE_CONSTRAINT,
	CHANGE_CONSTRAINT,

	CREATE_COLUMN_TYPE,
	DELETE_COLUMN_TYPE,
	CHANGE_COLUMN_TYPE
}