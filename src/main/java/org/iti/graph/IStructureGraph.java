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

package org.iti.graph;

import java.util.List;

import org.iti.graph.nodes.IStructureElement;
import org.jgrapht.graph.DefaultEdge;

public interface IStructureGraph {

	boolean containsElementWithPath(String path);

	IStructureElement getStructureElement(String identifier);

	DefaultEdge getEdge(String path);

	IStructureElement getSourceElement(DefaultEdge edge);

	IStructureElement getTargetElement(DefaultEdge edge);

	String getIdentifier(IStructureElement structureElement);

	String getPath(IStructureElement structureElement);

	String getPath(IStructureElement structureElement, boolean toRootElement);

	List<String> getIdentifiers();

	List<IStructureElement> getStructureElements(String path);

	List<IStructureElement> getStructureElements(String path, boolean directAncestorsOnly);

	List<String> getPathes();
}
