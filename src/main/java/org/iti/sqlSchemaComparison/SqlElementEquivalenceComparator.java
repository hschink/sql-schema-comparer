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
