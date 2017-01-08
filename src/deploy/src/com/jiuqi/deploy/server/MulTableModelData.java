package com.jiuqi.deploy.server;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jsqltool.model.CustomTableModel;

public class MulTableModelData {

	private List<String> colNames;
	private List<Class> classTypes;
	private List<Integer> colSizes;
	private Vector<Vector<Object>> data;
	private SQLSyntaxErrorException exception;
	private CustomTableModel model;

	public MulTableModelData() {
		colNames = new ArrayList<String>();
		classTypes = new ArrayList<Class>();
		colSizes = new ArrayList<Integer>();
		data = new Vector<Vector<Object>>();
	}

	public boolean inited() {
		return null != model;
	}

	public List<String> getColNames() {
		return colNames;
	}

	public List<Class> getClassTypes() {
		return classTypes;
	}

	public List<Integer> getColSizes() {
		return colSizes;
	}

	public Vector<Vector<Object>> getData() {
		return data;
	}

	public void append(String colName) {
		colNames.add(colName);
	}

	public void append(Class type) {
		classTypes.add(type);
	}

	public void append(int size) {
		colSizes.add(size);
	}

	public void append(String colName, Class type, int colsize) {
		append(colName);
		append(type);
		append(colsize);
	}

	public void appendRow(Vector<Object> row) {
		data.add(row);
	}

	public boolean hasSQLSyntaxError() {
		return null != exception;
	}

	public void setException(SQLSyntaxErrorException exception) {
		this.exception = exception;
	}

	public void buildTableModel() {
		this.model = new CustomTableModel(getColNames(), getClassTypes(), getColSizes());
	}
}
