package org.iti.graph.comparison;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.Map.Entry;

import org.iti.graph.comparison.StructureElementModification.Type;
import org.iti.graph.comparison.result.IModificationDetail;

public class StructureGraphComparerTestHelper {

	static void assertNodeModificationExpectations(
			Map<String, Type> expectedModifications,
			StructureGraphComparisonResult result) {
		for (Entry<String, Type> expectation : expectedModifications.entrySet()) {
			assertEquals(expectation.getKey(),
					expectation.getValue(),
					result.getModifications().get(expectation.getKey()).getType());
		}
	}

	public static void assertNodeModificationDetailExpectations(
			Map<String, IModificationDetail> expectedModificationDetails,
			StructureGraphComparisonResult result) {
		for (Entry<String, IModificationDetail> expectation : expectedModificationDetails.entrySet()) {
			IModificationDetail detail = expectation.getValue();
			IModificationDetail actualDetail = result.getModifications().get(expectation.getKey()).getModificationDetail();

			if (detail == null) {
				assertNull(expectation.getKey(), actualDetail);;
			} else {
				assertEquals(expectation.getKey(), detail.getPath(), detail.getPath());
				assertEquals(expectation.getKey(), detail.getIdentifier(), detail.getIdentifier());
			}
		}
	}

}
