package org.iti.sqlSchemaComparison;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;
import org.iti.sqlSchemaComparison.vertex.SqlElementFactory;
import org.iti.sqlSchemaComparison.vertex.SqlElementType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SqlSchemaComparisonResultTest {

	@Test
	public void sqlSchemaComparisonResultSerialization() throws IOException  {
		ISqlElement modifiedElement = SqlElementFactory.createSqlElement(SqlElementType.Table, "test");
		SqlSchemaComparisonResult result = new SqlSchemaComparisonResult();
		
		result.addModification(modifiedElement, SchemaModification.CREATE_TABLE);

		byte[] object = getResultSerializationString(result);
	    
	    assertTrue(object.toString().length() > 0);
	}

	@Test
	public void sqlSchemaComparisonResultDeserialization() throws IOException, ClassNotFoundException  {
		ISqlElement modifiedElement = SqlElementFactory.createSqlElement(SqlElementType.Table, "test");
		SqlSchemaComparisonResult result = new SqlSchemaComparisonResult();
		
		result.addModification(modifiedElement, SchemaModification.CREATE_TABLE);

		byte[] object = getResultSerializationString(result);
		result = getResultObject(object);
	    
	    assertTrue(result.getModifications().size() == 1);
	    assertTrue(result.getModifications().get(modifiedElement) == SchemaModification.CREATE_TABLE);
	}

	private byte[] getResultSerializationString(SqlSchemaComparisonResult result)
			throws IOException {
		ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);

	        oos.writeObject(result);
		} finally {
			oos.close();
		}

        return baos.toByteArray();
	}
	
	private SqlSchemaComparisonResult getResultObject(byte[] object)
			throws ClassNotFoundException, IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(object);
	    ObjectInputStream ois = new ObjectInputStream(bis);
	    
	    return (SqlSchemaComparisonResult)ois.readObject();
	}
}
