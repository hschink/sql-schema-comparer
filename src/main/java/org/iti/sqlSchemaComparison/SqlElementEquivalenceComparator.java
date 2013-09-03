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

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.jgrapht.Graph;
import org.jgrapht.experimental.equivalence.EquivalenceComparator;
import org.jgrapht.graph.DefaultEdge;

public class SqlElementEquivalenceComparator implements
		EquivalenceComparator<ISqlElement, Graph<ISqlElement, DefaultEdge>> {

	@Override
	public boolean equivalenceCompare(ISqlElement arg1, ISqlElement arg2,
			Graph<ISqlElement, DefaultEdge> context1,
			Graph<ISqlElement, DefaultEdge> context2) {

		return arg1.equals(arg2);
	}

	@Override
	public int equivalenceHashcode(ISqlElement arg1,
			Graph<ISqlElement, DefaultEdge> context) {

		return arg1.hashCode();
	}

}
