package org.iti.sqlSchemaComparison.reachability;

import java.util.List;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;

public interface ISqlElementReachabilityChecker {

	boolean isReachable();
	
	List<ISqlElement> getPath();
}
