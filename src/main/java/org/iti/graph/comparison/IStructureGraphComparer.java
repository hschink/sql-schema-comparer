package org.iti.graph.comparison;

import org.iti.graph.IStructureGraph;

public interface IStructureGraphComparer {

	StructureGraphComparisonResult compare(IStructureGraph oldGraph, IStructureGraph newGraph);
}
