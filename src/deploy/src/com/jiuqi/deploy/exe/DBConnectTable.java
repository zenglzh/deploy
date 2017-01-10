package com.jiuqi.deploy.exe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jiuqi.deploy.server.ArchiveLogEntry;
import com.jiuqi.deploy.ui.ArchiveLogTableModel;

public class DBConnectTable {
	private BlockingQueue<ArchiveLogEntry> rows;
	private Map<String, ArchiveLogEntry> codeIndexs;

	private ArchiveLogTableModel model;

	public DBConnectTable() {
		this.rows = new LinkedBlockingQueue<ArchiveLogEntry>(40);
		this.codeIndexs = new LinkedHashMap<String, ArchiveLogEntry>();
	}

	public ArchiveLogTableModel buildMode() {
		model = new ArchiveLogTableModel();
		int index = 0;
		for (String code : codeIndexs.keySet()) {
			ArchiveLogEntry iProduct = codeIndexs.get(code);
			iProduct.setRowIndex(index);
			model.addRow(iProduct);
			index++;
		}
		return model;
	}

	public ArchiveLogTableModel getModel() {
		return model;
	}

	public boolean inited() {
		return null != model;
	}

	public void putToQueue(ArchiveLogEntry e) throws InterruptedException {
		rows.put(e);
	}

	public void put(String code, ArchiveLogEntry product) {
		codeIndexs.put(code, product);
	}

	public ArchiveLogEntry get(String code) {
		return codeIndexs.get(code);
	}

	public BlockingQueue<ArchiveLogEntry> getRows() {
		return rows;
	}

	public int size() {
		return codeIndexs.size();
	}
}
