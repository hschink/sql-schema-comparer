package org.iti.sqlSchemaComparison.vertex;

import java.util.ArrayList;
import java.util.List;

import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;

public class SqlColumnVertex implements ISqlElement {

	@Override
	public SqlElementType getSqlElementType() {
		return SqlElementType.Column;
	}
	
	private String id = "";

	@Override
	public String getSqlElementId() {
		return id;
	}
	
	private String type = "";
	
	public String getType() {
		return type;
	}

	private List<IColumnConstraint> constraints = new ArrayList<>();

	public List<IColumnConstraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<IColumnConstraint> constraints) {
		this.constraints = constraints;
	}

	public SqlColumnVertex(String id, String type, List<IColumnConstraint> constraints) {
		this.id = id;
		this.type = type;
		this.constraints = constraints;
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
