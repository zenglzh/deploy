package com.jiuqi.deploy.server;

import java.util.Vector;

import com.jiuqi.deploy.intf.IProduct;

public class TableBody implements IProduct {

	private Vector<Object> row;
	private int rowIndex;

	public TableBody() {
		row = new Vector<Object>();
	}

	public TableBody(Vector<Object> row) {
		this.row = row;
	}

	@Override
	public boolean hasHeader() {
		return false;
	}

	public void setRow(Vector<Object> row) {
		this.row = row;
	}

	public Vector<Object> getRow() {
		return row;
	}

	@Override
	public int rowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
}
