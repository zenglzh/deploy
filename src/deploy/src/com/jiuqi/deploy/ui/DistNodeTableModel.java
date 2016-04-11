package com.jiuqi.deploy.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.jiuqi.deploy.server.ConfigDistEntity;

public class DistNodeTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 8916255176052091759L;
	private static final String[] HEAD = ConfigDistEntity.FieldTitle;

	private List<ConfigDistEntity> datas;

	public DistNodeTableModel() {
		datas = new ArrayList<ConfigDistEntity>();
	}

	public void addRow(ConfigDistEntity entity) {
		datas.add(entity);
	}

	public void insertRow(int rowNum, ConfigDistEntity entity) {
		datas.add(rowNum, entity);
	}

	public void removeRow(int rowNum) {
		datas.remove(rowNum);
	}

	public ConfigDistEntity get(int row) {
		return datas.get(row);
	}

	public void removeAll() {
		datas.removeAll(datas);
	}

	public void modify(int rowNum, ConfigDistEntity entity) {
		insertRow(rowNum, entity);
		removeRow(rowNum + 1);
		for (int i = 0; i < getColumnCount(); i++) {
			fireTableCellUpdated(rowNum, i);
		}

	}

	public List<ConfigDistEntity> getAll() {
		List<ConfigDistEntity> dest = new ArrayList<ConfigDistEntity>(
				Arrays.asList(new ConfigDistEntity[datas.size()]));
		Collections.copy(dest, datas);
		return dest;
	}

	@Override
	public int getRowCount() {
		return datas.size();
	}

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
			ConfigDistEntity configDistEntity = datas.get(rowIndex);
			return configDistEntity.getFieldValue(columnIndex);
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
