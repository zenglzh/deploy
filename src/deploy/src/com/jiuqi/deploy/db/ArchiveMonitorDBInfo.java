package com.jiuqi.deploy.db;

import com.jiuqi.deploy.util.ConnectionInfo;
import com.jiuqi.deploy.util.DatabaseConnectionInfo;

public class ArchiveMonitorDBInfo {
	private String provinceCode;
	private String dataSourceName;
	private String isUseCluster;
	private String url;// oracleURL
	private String dbRole;// normal或者sysdba
	private String userName;
	private String password;

	private double archiveSize;
	private double tableSpaceSize;
	private double dbInitSize;// 数据库分配大小
	private double archiveInitSize;// 归档日志分配大小
	private boolean isValid; // 判断数据库连接是否有效

	public ArchiveMonitorDBInfo() {
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getIsUseCluster() {
		return isUseCluster;
	}

	public void setIsUseCluster(String isUseCluster) {
		this.isUseCluster = isUseCluster;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDbRole() {
		return dbRole;
	}

	public void setDbRole(String dbRole) {
		this.dbRole = dbRole;
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

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public boolean isValid() {
		return isValid;
	}

	public DatabaseConnectionInfo getConnect() {
		ConnectionInfo connectionInfo = new ConnectionInfo();
		DatabaseConnectionInfo databaseConnectionInfo = connectionInfo.getDatabaseConnectionInfo();
		if (databaseConnectionInfo == null) {
			databaseConnectionInfo = new DatabaseConnectionInfo();
			connectionInfo.setDatabaseConnectionInfo(databaseConnectionInfo);
		}
		databaseConnectionInfo.setUrl(getUrl());
		databaseConnectionInfo.setUsername(getUserName());
		databaseConnectionInfo.setPassword(getPassword());
		databaseConnectionInfo.setConnectID(DatabaseConnectionInfo.CONNECTID[0]);
		connectionInfo.setName(databaseConnectionInfo.toString());// name
		return databaseConnectionInfo;
	}
}
