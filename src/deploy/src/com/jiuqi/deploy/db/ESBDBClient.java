package com.jiuqi.deploy.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.jiuqi.deploy.util.DatabaseConnectionInfo;
import com.jiuqi.deploy.util.TimeUtils;

/**
 *
 * @author esalaza
 */
public class ESBDBClient {

	static Logger logger = Logger.getLogger(ESBDBClient.class);

	public static final int ANY_STATUS = 0; // Incluye que la operacion no se
											// haya ejecutado, aunque sea parte
											// del flujo
	public static final int ERROR_STATUS = 1;
	public static final int FAULTED_STATUS = 2;
	public static final int RESUBMITABLE_STATUS = 3;
	public static final int OK_STATUS = 4;
	public static final int NOT_EXECUTED_OPERATION_STATUS = 5;
	public static final int EXECUTED_OPERATION_STATUS = 6;

	private String dbhost;
	private int dbport;
	private String database;
	private String user;
	private String pass;
	private String logon;// 连接身份
	private String url;
	private Connection conn = null;
	private ResultSet rs = null;
	private PreparedStatement pstmt = null;

	public static String[] CONNECTID = DatabaseConnectionInfo.CONNECTID;

	public ESBDBClient(DatabaseConnectionInfo db) {
		if (db.hasUrl()) {
			this.url = db.getUrl();
		} else {
			this.dbhost = db.getHost();
			this.dbport = Integer.parseInt(db.getPort());
			this.database = db.getSid();
		}
		this.user = db.getUsername();
		this.pass = db.getPassword();
		this.logon = db.getConnectID();
	}

	public ESBDBClient(String url, String user, String password, String logon) {
		this.url = url;
		this.user = user;
		this.pass = password;
		this.logon = logon;
	}

	public ESBDBClient(String dbhost, int dbport, String database, String user, String password, String logon) {
		this.dbhost = dbhost;
		this.dbport = dbport;
		this.database = database;
		this.user = user;
		this.pass = password;
		this.logon = logon;
		// connect();
		// esbDBClientSQL = new ESBDBClientSQL();
		// esbDBClientSQL.setConnection(conn);
	}

	public void connect() throws SQLException, ClassNotFoundException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Properties conProps = new Properties();
		conProps.put("user", user);
		conProps.put("password", pass);
		if (isDBALogin()) {
			conProps.put("internal_logon", "sysdba");
		}
		conn = DriverManager.getConnection(getUrl(), conProps);
	}

	public String getUrl() {
		if (null == url) {
			return "jdbc:oracle:thin:@" + dbhost + ":" + dbport + ":" + database;
		} else {
			return url;
		}
	}

	private boolean isDBALogin() {
		return null != this.logon && CONNECTID[1].equalsIgnoreCase(this.logon);
	}

	public void testConnect() throws Exception {
		connect();
		disconnect();
	}

	public boolean isValidConnection() {
		try {
			testConnect();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String getDatabase() {
		return database;
	}

	public Connection getConn() {
		return conn;
	}

	public void disconnect() throws SQLException {
		if (null != conn) {
			conn.close();
		}
	}

	public String getServiceGUIDByServiceName(String serviceName) throws Exception {
		logger.debug("getServiceGUIDByServiceName: " + serviceName);
		String guid = "";
		String sql = "Select GUID from oraesb.WF_EVENTS where NAME=? and TYPE='GROUP'";
		logger.debug(sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, serviceName);
		rs = pstmt.executeQuery();
		rs.next();
		guid = rs.getString("GUID");
		return guid;
	}

	public List<String> getOperationsGUIDsForService(String serviceGUID) throws SQLException {
		logger.debug("getOperationsGUIDsForService: " + serviceGUID);
		List<String> operationGUIDs = new ArrayList<String>();
		String sql = "select GUID from oraesb.WF_EVENTS where owner_guid=?";
		logger.debug(sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, serviceGUID);
		rs = pstmt.executeQuery();
		while (rs.next()) {
			operationGUIDs.add(rs.getString("GUID"));
		}
		return operationGUIDs;
	}

	public int getInstanceCount(String serviceName, String startDate, String endDate) throws Exception {
		logger.debug("getInstanceCount: " + serviceName + ", " + startDate + ", " + endDate);
		int count = 0;
		long sd = TimeUtils.getTime(startDate);
		long ed = TimeUtils.getTime(endDate);
		String guid = getServiceGUIDByServiceName(serviceName);
		String sql = "SELECT COUNT(*) AS COUNT FROM (SELECT FLOW_ID, MIN(TIMESTAMP) FROM (" + "\n" + "SELECT FLOW_ID, TIMESTAMP " + "\n" + "FROM oraesb.ESB_ACTIVITY B " + "\n" + "WHERE TIMESTAMP>=? "
				+ "\n" + "AND TIMESTAMP<=? " + "\n" + "AND (( SOURCE IN (?) OR OPERATION_GUID IN (?))) " + "\n" + "AND IS_STALE IS NULL " + "\n" + "ORDER BY TIMESTAMP DESC" + "\n" + ") " + "\n"
				+ "GROUP BY FLOW_ID " + "\n" + "ORDER BY MIN(TIMESTAMP) DESC)";
		logger.debug(sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setLong(1, sd);
		pstmt.setLong(2, ed);
		pstmt.setString(3, guid);
		pstmt.setString(4, guid);
		rs = pstmt.executeQuery();
		rs.next();
		count = rs.getInt("COUNT");
		return count;
	}

	public void createTablespace(OracleTablespaceProperties properties) throws SQLException, ClassNotFoundException {
		String buildSQL = properties.buildSQL();
		connect();
		Statement stmt = conn.createStatement();
		stmt.execute(buildSQL);
		stmt.close();
		disconnect();
	}

	public List<UserInfo> queryUsers() throws SQLException, ClassNotFoundException {
		List<UserInfo> userInfos = new ArrayList<UserInfo>();
		String sql = " select username,default_tablespace,temporary_tablespace from dba_users ";
		connect();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			UserInfo tInfo = new UserInfo();
			tInfo.setUsername(rs.getString("username"));
			tInfo.setDefaultTablespace(rs.getString("default_tablespace"));
			tInfo.setTemp_Tablespace(rs.getString("temporary_tablespace"));
			List<String> roles = queryRolePrivsByUsername(tInfo.getUsername());
			tInfo.setRolePries(roles);
			userInfos.add(tInfo);
		}
		stmt.close();
		disconnect();
		return userInfos;
	}

	public void createUser(UserProperties properties) throws SQLException, ClassNotFoundException {
		connect();
		Statement stmt = conn.createStatement();
		stmt.execute(properties.buildCreateUserSQL());
		stmt.close();
		disconnect();
	}

	public void modifyUser(UserProperties properties) throws ClassNotFoundException, SQLException {
		connect();
		Statement stmt = conn.createStatement();
		stmt.execute(properties.buildModifyUserSQL());
		stmt.close();
		disconnect();
	}

	public void grantUser(UserProperties properties) throws SQLException, ClassNotFoundException {
		connect();
		Statement stmt = conn.createStatement();
		if (null != properties.getRevokeprivilegs() && !properties.getRevokeprivilegs().isEmpty()) {
			stmt.execute(properties.buildRevokeGrantSQL());
		}
		if (null != properties.getPrivileges() && !properties.getPrivileges().isEmpty()) {
			stmt.execute(properties.buildGrantSQL());
		}
		stmt.close();
		disconnect();
	}

	public List<String> queryRolePrivsByUsername(String username) throws SQLException, ClassNotFoundException {
		List<String> list = new ArrayList<String>();
		String sql = " select granted_role from  dba_role_privs where grantee = '" + username + "' ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String granted_role = rs.getString("granted_role");
			list.add(granted_role);
		}
		stmt.close();
		return list;
	}

	public java.util.List<TablespaceInfo> queryTablespace() throws SQLException, ClassNotFoundException {
		java.util.List<TablespaceInfo> tablespaces = new ArrayList<TablespaceInfo>();
		String sql = " select name from v$tablespace ";
		connect();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String name = rs.getString("name");
			TablespaceInfo tInfo = new TablespaceInfo();
			tInfo.setName(name);
			tablespaces.add(tInfo);
		}
		stmt.close();
		disconnect();
		return tablespaces;
	}

}
