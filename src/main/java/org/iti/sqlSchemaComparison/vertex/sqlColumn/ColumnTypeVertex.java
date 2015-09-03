package org.iti.sqlSchemaComparison.vertex.sqlColumn;

import org.iti.sqlSchemaComparison.vertex.ISqlElement;

public class ColumnTypeVertex implements ISqlElement {

	private static final long serialVersionUID = 2561267699111344106L;

	private String columnName;
	private String columnType;

	public ColumnTypeVertex(String columnName, String columnType) {
		this.columnName = columnName;
		this.columnType = columnType;
	}

	@Override
	public String getName() {
		StringBuilder builder = new StringBuilder();

		builder.append("[ColumnType]");
		builder.append(" ");
		builder.append(columnName);
		builder.append(".");
		builder.append(columnType);

		return builder.toString();
	}

	public String getColumnType() {
		return columnType;
	}

	@Override
	public boolean isMandatory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOptionalList() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getSourceElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSourceElement(Object sourceElement) {
		// TODO Auto-generated method stub

	}

}
