package com.jiuqi.deploy.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.jiuqi.deploy.server.ArchiveLogEntry;

public class ArchiveLogTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8916255176052091719L;
	private static final String[] HEAD = ArchiveLogEntry.FieldTitle;
	static final int[] SIZE = ArchiveLogEntry.FieldSize;

	private List<ArchiveLogEntry> datas;

	public ArchiveLogTableModel() {
		datas = new ArrayList<ArchiveLogEntry>();
	}

	public void addRow(ArchiveLogEntry entity) {
		datas.add(entity);
	}

	public void insertRow(int rowNum, ArchiveLogEntry entity) {
		datas.add(rowNum, entity);
	}

	public void removeRow(int rowNum) {
		datas.remove(rowNum);
	}

	public ArchiveLogEntry get(int row) {
		return datas.get(row);
	}

	public void removeAll() {
		datas.removeAll(datas);
	}

	public void modify(int rowNum, ArchiveLogEntry entity) {
		insertRow(rowNum, entity);
		removeRow(rowNum + 1);
		for (int i = 0; i < getColumnCount(); i++) {
			fireTableCellUpdated(rowNum, i);
		}
	}

	public List<ArchiveLogEntry> getAll() {
		List<ArchiveLogEntry> dest = new ArrayList<ArchiveLogEntry>(Arrays.asList(new ArchiveLogEntry[datas.size()]));
		Collections.copy(dest, datas);
		return dest;
	}

	@Override
	public int getRowCount() {
		return datas.size();
	}

	@Override
	public String getColumnName(int col) {
		return HEAD[col];
	}

	@Override
	public int getColumnCount() {
		return HEAD.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < getRowCount() && columnIndex < getColumnCount()) {
			ArchiveLogEntry archiveLogEntry = datas.get(rowIndex);
			return archiveLogEntry.getFieldValue(rowIndex, columnIndex);
		}
		return null;
	}

	public Class getColumnClass(int c) {
		Object valueAt = getValueAt(0, c);
		if (null != valueAt) {
			return valueAt.getClass();
		}
		return String.class;
	}

}
