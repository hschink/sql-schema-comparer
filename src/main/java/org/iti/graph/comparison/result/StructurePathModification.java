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

import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.graph.DefaultEdge;


public class StructurePathModification implements IStructureModification {

	private static final List<Type> VALID_TYPES = new ArrayList<Type>() {
		private static final long serialVersionUID = -2162117789613361300L;
	{
		add(Type.None);
		add(Type.PathAdded);
		add(Type.PathDeleted);
		add(Type.PathRenamed);
	}};

	private String identifier;

	@Override
	public String getIdentifier() {
		return identifier;
	}

	private DefaultEdge edge;

	public DefaultEdge getEdge() {
		return edge;
	}

	private IStructureElement sourceElement;

	public IStructureElement getSourceElement() {
		return sourceElement;
	}

	private IStructureElement targetElement;

	public IStructureElement getTargetElement() {
		return targetElement;
	}

	private Type type;

	@Override
	public Type getType() {
		return type;
	}

	private IModificationDetail modificationDetail;

	public IModificationDetail getModificationDetail() {
		return modificationDetail;
	}

	public StructurePathModification(String identifier,
			DefaultEdge edge,
			IStructureElement sourceElement,
			IStructureElement targetElement,
			Type type) {
		this.identifier = identifier;
		this.edge = edge;
		this.sourceElement = sourceElement;
		this.targetElement = targetElement;
		this.type = type;

		if (!VALID_TYPES.contains(type)) {
			throw new IllegalArgumentException(String.format("Type %s is invalid for StructureElementModification!", type));
		}
	}

	public StructurePathModification(String identifier,
			DefaultEdge edge,
			IStructureElement sourceElement,
			IStructureElement targetElement,
			Type type,
			IModificationDetail modificationDetail) {
		this(identifier, edge, sourceElement, targetElement, type);

		this.modificationDetail = modificationDetail;
	}
}