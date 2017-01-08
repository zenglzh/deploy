package com.jiuqi.deploy.exe;

import com.jiuqi.deploy.server.ArchiveLogEntry;

public class DBArchiveThread implements Runnable {
	private ConnectPipe pipe;

	public DBArchiveThread(ConnectPipe pipe) {
		this.pipe = pipe;

	}

	@Override
	public void run() {
		ArchiveLogEntry entry = pipe.getEntry().computeDBSize();
		// pipe.getTableWrapper().modify(pipe.getRowIndex(), entry);
		pipe.getTableWrapper().add(entry);
		pipe.getTableWrapper().getTable().repaint();
	}

}
