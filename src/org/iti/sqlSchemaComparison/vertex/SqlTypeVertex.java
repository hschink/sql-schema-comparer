package org.iti.sqlSchemaComparison.vertex;

public class SqlTypeVertex implements ISqlElement {

	@Override
	public SqlElementType getSqlElementType() {
		return SqlElementType.Type;
	}
	
	private String id = "";

	@Override
	public String getSqlElementId() {
		// TODO Auto-generated method stub
		return id;
	}

	public SqlTypeVertex(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "[" + getSqlElementType() + "] " + getSqlElementId();
	}

	@Override
	public boolean equals(Object o) {
		return SqlElementFactory.equals(this, o);
	}
	
	@Override
	public int hashCode() {
		return SqlElementFactory.hashCode(this);
	}
}