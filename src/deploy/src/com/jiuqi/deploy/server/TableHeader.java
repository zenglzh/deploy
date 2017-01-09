package com.jiuqi.deploy.server;

import java.util.ArrayList;
import java.util.List;

public class TableHeader {

	private List<String> colNames;
	private List<Class<?>> classTypes;
	private List<Integer> colSizes;

	public TableHeader() {
		colNames = new ArrayList<String>();
		classTypes = new ArrayList<Class<?>>();
		colSizes = new ArrayList<Integer>();
	}

	public boolean inited() {
		return !colNames.isEmpty() && !classTypes.isEmpty();
	}

	public List<String> getColNames() {
		return colNames;
	}

	public List<Class<?>> getClassTypes() {
		return classTypes;
	}

	public List<Integer> getColSizes() {
		return colSizes;
	}

	public void append(String colName) {
		colNames.add(colName);
	}

	public void append(Class<?> type) {
		classTypes.add(type);
	}

	public void append(int size) {
		colSizes.add(size);
	}

	public void append(String colName, Class<?> type, int colsize) {
		append(colName);
		append(type);
		append(colsize);
	}

}
