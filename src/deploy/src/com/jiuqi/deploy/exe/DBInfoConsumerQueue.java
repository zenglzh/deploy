package com.jiuqi.deploy.exe;

import java.util.concurrent.BlockingQueue;

import com.jiuqi.deploy.server.ArchiveLogEntry;
import com.jiuqi.deploy.ui.ArchiveLogTableModel;
import com.jiuqi.deploy.ui.ArchiveLogTableWrapper;

public class DBInfoConsumerQueue implements Runnable {

	private DBConnectTable connectTable;
	private ArchiveLogTableWrapper tableWrapper;

	public DBInfoConsumerQueue(DBConnectTable connectTable, ArchiveLogTableWrapper tableWrapper) {
		this.connectTable = connectTable;
		this.tableWrapper = tableWrapper;
	}

	@Override
	public void run() {
		initTable();
		takeRow();
	}

	private void initTable() {
		while (!connectTable.inited()) {
			Thread.yield();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		tableWrapper.initUI(connectTable.getModel());

	}

	private void takeRow() {
		BlockingQueue<ArchiveLogEntry> rows = connectTable.getRows();
		for (int i = 0; i < connectTable.size(); i++) {
			try {
				ArchiveLogEntry product = rows.take();
				ArchiveLogTableModel model = connectTable.getModel();
				model.modify(product.getRowIndex(), product);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
