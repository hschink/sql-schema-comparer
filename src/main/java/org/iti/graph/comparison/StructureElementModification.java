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

package org.iti.graph.comparison;

import org.iti.graph.comparison.result.IModificationDetail;

public class StructureElementModification {
	public enum Type {
		None,
		NodeAdded,
		NodeMoved,
		NodeRenamed,
		NodeDeleted,
	}

	private String path;

	public String getPath() {
		return path;
	}

	private String identifier;

	public String getIdentifier() {
		return identifier;
	}

	private Type type;

	public Type getType() {
		return type;
	}

	private IModificationDetail modificationDetail;

	public IModificationDetail getModificationDetail() {
		return modificationDetail;
	}

	public StructureElementModification(String path, String identifier, Type type) {
		this.path = path;
		this.identifier = identifier;
		this.type = type;
	}

	public StructureElementModification(String path,
			String identifier,
			Type type,
			IModificationDetail modificationDetail) {
		this(path, identifier, type);

		this.modificationDetail = modificationDetail;
	}
}