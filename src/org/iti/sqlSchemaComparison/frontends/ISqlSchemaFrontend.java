package org.iti.sqlSchemaComparison.frontends;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

public interface ISqlSchemaFrontend {

	Graph<ISqlElement, DefaultEdge> createSqlSchema();
}
