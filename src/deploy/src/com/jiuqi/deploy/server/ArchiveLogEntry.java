/*
 * @(#)ArchiveLogEntry.java  
 */
package com.jiuqi.deploy.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;

import org.jsqltool.conn.DbConnection;

import com.jiuqi.deploy.db.Constant;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.db.JDBCUtil;

/**
 * @author: zenglizhi
 * @time: 2016年12月27日
 */
public class ArchiveLogEntry implements Runnable {
	public enum CONNECT {
		CONNECTING("连接中"), CONNECTED("已连接"), UNCONNECTED("未连接"), ;
		String status;

		CONNECT(String status) {
			this.status = status;
		}

		String getStatus() {
			return status;
		}
	}

	private String pcode;
	private String dataSource;
	private String url;
	private CONNECT connected;// 0,1,2
	private double archiveSize;
	private double tableSpaceSize;
	private double dbInitSize;// 数据库分配大小
	private double archiveInitSize;// 归档日志分配大小
	// 0 1 2 3 4 5 6 7 8 9 10
	public static final String[] FieldTitle = { "序号", "省代码", "数据源名称", "数据库地址", "连接状态", "数据库分配（G）", "已使用库空间（G）", "已使用库空间比(%)", "归档日志分配（G）", "已使用归档日志（G）", "已使用归档日志比（%）" };
	public static final int[] FieldSize = { 10, 32, 29, 40, 20, 35, 39, 43, 43, 45, 46 };

	public final static DecimalFormat df = new DecimalFormat("#.#");

	public Object getFieldValue(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.valueOf(rowIndex + 1);
		case 1:
			return pcode;
		case 2:
			return dataSource;
		case 3:
			return url;
		case 4:
			return connected.status;
		case 5:
			return dbInitSize;
		case 6:
			return tableSpaceSize;
		case 7:
			if (dbInitSize == 0) {
				return "-";
			} else {
				double tablerate = tableSpaceSize / dbInitSize * 100;
				String format = df.format(tablerate);
				return format + "%";
			}
		case 8:
			return archiveInitSize;
		case 9:
			return archiveSize;
		case 10:
			if (archiveSize == 0) {
				return "-";
			} else if (archiveInitSize == 0) {
				return "--";
			} else {
				double archiveUsesRate = archiveSize / archiveInitSize * 100;
				String format = df.format(archiveUsesRate);
				return format + "%";
			}
		default:
			return "";
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setConnected(CONNECT connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return CONNECT.CONNECTED.equals(this.connected);
	}

	public CONNECT getConnected() {
		return connected;
	}

	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public double getArchiveSize() {
		return archiveSize;
	}

	public void setArchiveSize(double archiveSize) {
		this.archiveSize = archiveSize;
	}

	public double getTableSpaceSize() {
		return tableSpaceSize;
	}

	public void setTableSpaceSize(double tableSpaceSize) {
		this.tableSpaceSize = tableSpaceSize;
	}

	public double getDbInitSize() {
		return dbInitSize;
	}

	public void setDbInitSize(double dbInitSize) {
		this.dbInitSize = dbInitSize;
	}

	public double getArchiveInitSize() {
		return archiveInitSize;
	}

	public void setArchiveInitSize(double archiveInitSize) {
		this.archiveInitSize = archiveInitSize;
	}

	public static String[] getFieldtitle() {
		return FieldTitle;
	}

	public static int[] getFieldsize() {
		return FieldSize;
	}

	public DbConnection getDBConnection() {
		return null;
	}

	private String user;
	private String pwd;
	private String logon;

	public void setUser(String user) {
		this.user = user;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setLogon(String logon) {
		this.logon = logon;
	}

	@Override
	public void run() {
		computeDBSize();
	}

	public ArchiveLogEntry computeDBSize() {
		Connection conn = null;
		try {
			ESBDBClient dbClient = new ESBDBClient(url, user, pwd, logon);
			dbClient.connect();
			conn = dbClient.getConn();
			double archiveSize = JDBCUtil.getDbOneFieldSize(conn, Constant.sql_archive);
			setConnected(CONNECT.CONNECTED);
			setArchiveSize(archiveSize);
			setTableSpaceSize(JDBCUtil.getDbOneFieldSize(conn, Constant.sql_tableSpace));
		} catch (Exception e) {
			setConnected(CONNECT.UNCONNECTED);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return this;
	}

}
