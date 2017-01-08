package com.jiuqi.deploy.exe;

import com.jiuqi.deploy.server.ArchiveLogEntry;
import com.jiuqi.deploy.ui.ArchiveLogTableWrapper;

public class ConnectPipe {

	private int rowIndex;
	private ArchiveLogEntry entry;
	private ArchiveLogTableWrapper tableWrapper;

	public ConnectPipe(int index, ArchiveLogEntry entry, ArchiveLogTableWrapper tableWrapper) {
		this.rowIndex = index;
		this.entry = entry;
		this.tableWrapper = tableWrapper;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public ArchiveLogEntry getEntry() {
		return entry;
	}

	public void setEntry(ArchiveLogEntry entry) {
		this.entry = entry;
	}

	public ArchiveLogTableWrapper getTableWrapper() {
		return tableWrapper;
	}

	public void setTableWrapper(ArchiveLogTableWrapper tableWrapper) {
		this.tableWrapper = tableWrapper;
	}

}
