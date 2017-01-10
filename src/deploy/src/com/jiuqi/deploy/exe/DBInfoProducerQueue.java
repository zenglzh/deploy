package com.jiuqi.deploy.exe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jiuqi.deploy.db.ArchiveMonitorDBInfo;
import com.jiuqi.deploy.db.Constant;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.db.JDBCUtil;
import com.jiuqi.deploy.server.ArchiveLogEntry;
import com.jiuqi.deploy.server.ArchiveLogEntry.CONNECT;
import com.jiuqi.deploy.util.IMonitor;

public class DBInfoProducerQueue extends Thread {

	private IMonitor monitor;

	private DBConnectTable connectTable;

	private ESBDBClient dbClient;

	private ArchiveMonitorDBInfo dbInfo;

	public DBInfoProducerQueue(DBConnectTable connectTable, ArchiveMonitorDBInfo dbInfo, IMonitor monitor) {
		this.connectTable = connectTable;
		this.monitor = monitor;
		this.dbInfo = dbInfo;
		this.dbClient = new ESBDBClient(dbInfo.getConnect());
		ArchiveLogEntry entry = new ArchiveLogEntry();
		entry.setPcode(dbInfo.getProvinceCode());
		entry.setUrl(dbInfo.getUrl());
		entry.setUserName(dbInfo.getUserName());
		entry.setPassword(dbInfo.getPassword());
		entry.setDataSource(dbInfo.getDataSourceName());
		entry.setDbInitSize(dbInfo.getDbInitSize());
		entry.setArchiveInitSize(dbInfo.getArchiveInitSize());
		entry.setConnected(CONNECT.CONNECTING);
		this.connectTable.put(dbInfo.getProvinceCode(), entry);
	}

	@Override
	public void run() {
		ResultSet rset = null;
		Statement stmt = null;
		try {
			dbClient.connect();
			ArchiveLogEntry entry = connectTable.get(dbInfo.getProvinceCode());
			double archiveSize = JDBCUtil.getDbOneFieldSize(dbClient.getConn(), Constant.sql_archive);
			entry.setConnected(CONNECT.CONNECTED);
			entry.setArchiveSize(archiveSize);
			entry.setTableSpaceSize(JDBCUtil.getDbOneFieldSize(dbClient.getConn(), Constant.sql_tableSpace));
			connectTable.putToQueue(entry);
		} catch (Exception e) {
			e.printStackTrace();
			buildEmptyBodyData();
			monitor.error(dbInfo.getProvinceCode() + ":" + e.getMessage());
		} finally {
			try {
				if (null != rset) {
					rset.close();
				}
				if (null != stmt) {
					stmt.close();
				}
				dbClient.disconnect();
				monitor.finish();
			} catch (SQLException e) {
			}
		}
	}

	private void buildEmptyBodyData() {
		ArchiveLogEntry entry = connectTable.get(dbInfo.getProvinceCode());
		entry.setConnected(CONNECT.UNCONNECTED);
		try {
			connectTable.putToQueue(entry);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
