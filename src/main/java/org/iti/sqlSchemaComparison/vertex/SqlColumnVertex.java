package org.iti.sqlSchemaComparison.vertex;

import java.util.ArrayList;
import java.util.List;

import org.iti.sqlSchemaComparison.vertex.sqlColumn.IColumnConstraint;

public class SqlColumnVertex implements ISqlElement {

	private Object sourceElement = null;
	
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
	
	private String table = "";

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	private List<IColumnConstraint> constraints = new ArrayList<>();

	public List<IColumnConstraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<IColumnConstraint> constraints) {
		this.constraints = constraints;
	}

	public SqlColumnVertex(String id, String type, String table) {
		this.id = id;
		this.type = type;
		this.table = table;
	}
	
	@Override
	public String toString() {
		return "[" + getSqlElementType() + "] " + table + "." + getSqlElementId();
	}

	@Override
	public boolean equals(Object o) {
		return SqlElementFactory.equals(this, o);
	}
	
	@Override
	public int hashCode() {
		return SqlElementFactory.hashCode(this);
	}

	@Override
	public Object getSourceElement() {
		return sourceElement;
	}

	@Override
	public void setSourceElement(Object sourceElement) {
		this.sourceElement = sourceElement;
	}
}
