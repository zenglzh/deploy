package org.jsqltool.model;

import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * <p>
 * Title: JSqlTool Project
 * </p>
 * <p>
 * Description: Table Model, whose editability can be swiched.
 * </p>
 * <p>
 * Copyright: Copyright (C) 2006 Mauro Carniel
 * </p>
 *
 * <p>
 * This file is part of JSqlTool project. This library is free software; you can
 * redistribute it and/or modify it under the terms of the (LGPL) Lesser General
 * Public License as published by the Free Software Foundation;
 *
 * GNU LESSER GENERAL PUBLIC LICENSE Version 2.1, February 1999
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * The author may be contacted at: maurocarniel@tin.it
 * </p>
 *
 * @author Mauro Carniel
 * @version 1.0
 */
public class CustomTableModel extends DefaultTableModel {

	public static final int DETAIL_REC = 0;
	public static final int INSERT_REC = 1;
	public static final int EDIT_REC = 2;

	private int editMode = DETAIL_REC;
	private int insertRowIndex = -1;
	private boolean[] editableCols = null;
	private Vector colNames = new Vector();
	private Class[] colTypes = null;
	private int[] colSizes = null;

	public CustomTableModel(String[] colNames, Class[] colTypes) {
		this(colNames, colTypes, null);
	}

	public CustomTableModel(List<String> colNames, List<Class> colTypes, List<Integer> colSizes) {
		this(colNames.toArray(new String[colNames.size()]), colTypes.toArray(new Class[colTypes.size()]), getColsize(colSizes));

	}

	private static int[] getColsize(List<Integer> colSizes) {
		int[] a = new int[colSizes.size()];

		for (int i = 0; i < colSizes.size(); i++) {
			a[i] = colSizes.get(i);
		}
		return a;
	}

	public CustomTableModel(String[] colNames, Class[] colTypes, int[] colSizes) {
		super(colNames, 0);
		for (int i = 0; i < colNames.length; i++) {
			this.colNames.add(colNames[i]);
		}
		this.colTypes = colTypes;
		if (colSizes == null) {
			colSizes = new int[colNames.length];
			for (int i = 0; i < colNames.length; i++)
				colSizes[i] = colNames[i].length() * 10;
		}
		this.colSizes = colSizes;
	}

	public void addColumn(Object columnName, Vector columnData, Class colType) {
		this.addColumn(columnName, columnData, colType, columnName.toString().length() * 10);
	}

	public void addColumn(Object columnName, Vector columnData, Class colType, int colSize) {
		super.addColumn(columnName, columnData);

		Class[] newColTypes = new Class[colTypes.length + 1];
		System.arraycopy(colTypes, 0, newColTypes, 0, colTypes.length);
		newColTypes[colTypes.length] = colType;
		colTypes = newColTypes;

		int[] newColSizes = new int[colSizes.length + 1];
		System.arraycopy(colSizes, 0, newColSizes, 0, colSizes.length);
		newColSizes[colSizes.length] = colSize;
		colSizes = newColSizes;
	}

	public void addRow(Vector rowData) {
		super.addRow(rowData);
	}

	public void insertRow(int index, Vector rowData) {
		super.insertRow(index, rowData);
	}

	public Class getColumnClass(int col) {
		return colTypes[col].equals(java.sql.Blob.class) ? String.class : colTypes[col];
	}

	public boolean isBlob(int col) {
		return colTypes[col].equals(java.sql.Blob.class);
	}

	/**
	 * Returns true regardless of parameter values.
	 *
	 * @param row
	 *            the row whose value is to be queried
	 * @param column
	 *            the column whose value is to be queried
	 * @return true
	 * @see #setValueAt
	 */
	public boolean isCellEditable(int row, int column) {
		if (editMode == DETAIL_REC || isBlob(column))
			return false;
		else if (editMode == INSERT_REC)
			return (row == insertRowIndex);
		else
			return editableCols[column];
	}

	public void setEditMode(int editMode) {
		this.editMode = editMode;
		if (editMode == INSERT_REC) {
			this.addRow(new Object[this.getColumnCount()]);
			insertRowIndex = this.getRowCount() - 1;
		} else
			insertRowIndex = -1;
		if (editMode == EDIT_REC && editableCols == null) {
			editableCols = new boolean[colNames.size()];
			for (int i = 0; i < colNames.size(); i++)
				editableCols[i] = true;
		}
	}

	public void setEditableCols(boolean[] editableCols) {
		this.editableCols = editableCols;
	}

	public void setDataVector(Vector data) {
		this.setDataVector(data, colNames);
	}

	public int getEditMode() {
		return editMode;
	}

	public int[] getColSizes() {
		return colSizes;
	}

}