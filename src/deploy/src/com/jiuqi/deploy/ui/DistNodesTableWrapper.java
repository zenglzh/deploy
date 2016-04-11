package com.jiuqi.deploy.ui;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JTable;

import com.jiuqi.deploy.intf.IDistNodeCallback;
import com.jiuqi.deploy.server.ConfigDistEntity;

public class DistNodesTableWrapper implements IDistNodeCallback {
	private JTable table;
	private DistNodeTableModel tablemode;
	public DistNodesTableWrapper() {
		initUI();
	}

	private void initUI() {
		tablemode = new DistNodeTableModel();
		table = new JTable(tablemode);
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
		table.getColumnModel().getColumn(1).setPreferredWidth(45);
		table.getColumnModel().getColumn(2).setPreferredWidth(45);
		table.getColumnModel().getColumn(3).setPreferredWidth(65);
		table.getColumnModel().getColumn(4).setPreferredWidth(30);
		table.setPreferredScrollableViewportSize(new Dimension(400, 80));
	}

	public JTable getTable() {
		return table;
	}
	

	public ConfigDistEntity get(int row) {
		return tablemode.get(row);
	}

	public void addRow(ConfigDistEntity entity) {
		tablemode.addRow(entity);
		table.revalidate();
	}

	public void deleteRow(int rowcount) {
		tablemode.removeRow(rowcount);
		table.revalidate();
	}

	public void removeAll() {
		tablemode.removeAll();
		table.revalidate();
	}

	public List<ConfigDistEntity> getAll() {
		return tablemode.getAll();
	}

	public void modifyRow(int row, ConfigDistEntity entity) {
		tablemode.modify(row, entity);
		table.revalidate();
	}


	@Override
	public boolean modify(int row, ConfigDistEntity entity) {
		modifyRow(row, entity);
		return true;
	}

	@Override
	public boolean add(ConfigDistEntity entity) {
		addRow(entity);
		return true;
	}

}
