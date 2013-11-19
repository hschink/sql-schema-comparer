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

package org.iti.graph.comparison.result;

import java.util.ArrayList;
import java.util.List;


public class StructureElementModification implements IStructureModification {

	private static final List<Type> VALID_TYPES = new ArrayList<Type>() {
		private static final long serialVersionUID = -2162117789613361300L;
	{
		add(Type.None);
		add(Type.NodeAdded);
		add(Type.NodeDeleted);
		add(Type.NodeMoved);
		add(Type.NodeRenamed);
	}};

	private String path;

	public String getPath() {
		return path;
	}

	private String identifier;

	@Override
	public String getIdentifier() {
		return identifier;
	}

	private Type type;

	@Override
	public Type getType() {
		return type;
	}

	private IModificationDetail modificationDetail;

	@Override
	public IModificationDetail getModificationDetail() {
		return modificationDetail;
	}

	public StructureElementModification(String path, String identifier, Type type) {
		this.path = path;
		this.identifier = identifier;
		this.type = type;

		if (!VALID_TYPES.contains(type)) {
			throw new IllegalArgumentException(String.format("Type %s is invalid for StructureElementModification!", type));
		}
	}

	public StructureElementModification(String path,
			String identifier,
			Type type,
			IModificationDetail modificationDetail) {
		this(path, identifier, type);

		this.modificationDetail = modificationDetail;
	}
}