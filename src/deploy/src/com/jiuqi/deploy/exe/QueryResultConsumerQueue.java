package com.jiuqi.deploy.exe;

import java.util.concurrent.BlockingQueue;

import org.jsqltool.gui.graphics.DateCellEditor;
import org.jsqltool.gui.graphics.DateCellRenderer;
import org.jsqltool.gui.panel.Table;
import org.jsqltool.model.CustomTableModel;
import org.jsqltool.utils.Options;

import com.jiuqi.deploy.intf.IProduct;

public class QueryResultConsumerQueue implements Runnable {
	private final BlockTable btable;
	private Table table;

	public QueryResultConsumerQueue(BlockTable btable, Table table) {

		this.btable = btable;
		this.table = table;
	}

	@Override
	public void run() {
		initedHeader();
		initTable();
		takeRow();
	}

	private void initedHeader() {
		while (!btable.initedHeader()) {
			Thread.yield();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void takeRow() {
		BlockingQueue<IProduct> rows = btable.getRows();
		for (int i = 0; i < btable.size(); i++) {
			try {
				IProduct product = rows.take();
				CustomTableModel model = btable.getModel();
				model.modify(product.rowIndex(), product.getRow());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void initTable() {
		table.setModel(btable.getModel());
		try {
			for (int i = 0; i < table.getColumnCount(); i++) {
				if (((CustomTableModel) table.getModel()).getColumnClass(table.convertColumnIndexToModel(i)).equals(java.util.Date.class)
						|| ((CustomTableModel) table.getModel()).getColumnClass(table.convertColumnIndexToModel(i)).equals(java.sql.Date.class)
						|| ((CustomTableModel) table.getModel()).getColumnClass(table.convertColumnIndexToModel(i)).equals(java.sql.Timestamp.class)) {
					table.getColumnModel().getColumn(i).setCellEditor(new DateCellEditor());
					table.getColumnModel().getColumn(i).setCellRenderer(new DateCellRenderer());
					table.getColumnModel().getColumn(i).setPreferredWidth(6 * Options.getInstance().getDateFormat().length());
				} else
					table.getColumnModel().getColumn(i).setPreferredWidth(((CustomTableModel) table.getModel()).getColSizes()[table.convertColumnIndexToModel(i)]);
			}
			if (table.getRowCount() > 0)
				table.setRowSelectionInterval(0, 0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
