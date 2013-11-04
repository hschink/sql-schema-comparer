package org.iti.graph.comparison;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

public class StructureGraphComparerTestHelper {

	static void assertNodeModificationExpectations(
			Map<String, StructureElementModification> expectedModifications,
			StructureGraphComparisonResult result) {
		for (Entry<String, StructureElementModification> expectation : expectedModifications.entrySet()) {
			assertEquals(expectation.getKey(),
					expectation.getValue(),
					result.getModifications().get(expectation.getKey()));
		}
	}

}
