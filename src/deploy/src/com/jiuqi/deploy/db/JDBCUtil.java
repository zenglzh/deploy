package com.jiuqi.deploy.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jiuqi.deploy.util.DatabaseConnectionInfo;

public class JDBCUtil {

	public static boolean testConnection(DatabaseConnectionInfo info) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("驱动加载失败，原因：" + e.getMessage(), e);
		}
		boolean result = false;
		try {
			Connection con = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
			if (con != null)
				result = true;
			con.close();
			con = null;
		} catch (SQLException e) {
			System.err.println("SQLException: " + e);
			throw new Exception("连接数据库失败，原因：" + e.getMessage(), e);
		}
		return result;
	}

	public static void createTablespace(String conn_alias, String ts_name) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("驱动加载失败，原因：" + e.getMessage(), e);
		}

		try {
			DatabaseConnectionInfo info = null;// DBConfig.getConnInfoByAlias(conn_alias);
			Connection con = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
			String createTableSpace = "CREATE TABLESPACE " + ts_name + " LOGGING DATAFILE '" + ts_name
					+ ".DBF' SIZE 100M AUTOEXTEND ON NEXT 200M MAXSIZE 20480M EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO";
			System.out.println("创建表空间:" + createTableSpace);
			Statement stmt = con.createStatement();
			stmt.execute(createTableSpace);

			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.err.println("SQLException: " + e);
			throw new Exception("创建表空间失败，原因：" + e.getMessage(), e);
		}
	}

	public static void createUser(String conn_alias, String username, String password, String tablespace) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("驱动加载失败，原因：" + e.getMessage(), e);
		}

		try {
			DatabaseConnectionInfo info = null;// DBConfig.getConnInfoByAlias(conn_alias);
			Connection con = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
			String createUser = "CREATE USER " + username + " IDENTIFIED BY " + password + " DEFAULT TABLESPACE " + tablespace + " TEMPORARY TABLESPACE TEMP";

			String roles = "CONNECT,RESOURCE,exp_full_database,imp_full_database";
			String grant = "GRANT " + roles + " TO " + username;

			Statement stmt = con.createStatement();
			stmt.execute(createUser);
			stmt.execute(grant);

			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.err.println("SQLException: " + e);
			throw new Exception("创建用户失败，原因：" + e.getMessage(), e);
		}
	}

	public static String exportDump(DatabaseConnectionInfo info, String filePath) throws Exception {
		if (null == info) {
			throw new Exception("数据库未连接。");
		}
		try {
			String cmdStr = "exp " + info.getUsername() + "/" + info.getPassword() + "@//" + info.getHost() + ":" + info.getPort() + "/" + info.getSid() + " file=\"" + filePath + "\" ";
			System.out.println(cmdStr);
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmdStr);
			String line = null;
			StringBuffer buffer = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
			// 读取ErrorStream很关键，这个解决了挂起的问题。
			while ((line = br.readLine()) != null) {
				System.err.println(line);
				buffer.append(line).append("<br />");
			}
			br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				buffer.append(line).append("<br />");
			}
			process.waitFor();
			// if (process.waitFor() != 0) {
			// throw new Exception("导出失败");
			// }
			return buffer.toString();

		} catch (Exception e) {
			System.err.println("SQLException: " + e);
			throw new Exception("导出失败，原因：" + e.getMessage(), e);
		}
	}

	public static String importDump(DatabaseConnectionInfo info, String filePath, String username) throws Exception {
		try {
			// DatabaseConnectionInfo info = null;//
			// DBConfig.getConnInfoByAlias(conn_alias);
			String cmdStr = "imp " + info.getUsername() + "/" + info.getPassword() + "@//" + info.getHost() + ":" + info.getPort() + "/" + info.getSid() + " file=\"" + filePath + "\" " + " fromuser="
					+ username + " touser=" + info.getUsername();
			System.out.println(cmdStr);
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmdStr);
			String line = null;
			StringBuffer buffer = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
			// 读取ErrorStream很关键，这个解决了挂起的问题。
			while ((line = br.readLine()) != null) {
				System.err.println(line);
				buffer.append(line).append("<br />");
			}
			br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				buffer.append(line).append("<br />");
			}
			process.waitFor();
			return buffer.toString();
		} catch (Exception e) {
			System.err.println("SQLException: " + e);
			throw new Exception("导入失败，原因：" + e.getMessage(), e);
		}
	}

	public static List querySessions(DatabaseConnectionInfo info) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("驱动加载失败，原因：" + e.getMessage(), e);
		}
		List list = null;
		try {
			Connection con = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
			String sql = "select saddr,sid,serial#,username,status,to_char(logon_time,'yyyy-mm-dd hh24:mi:ss') as logon_time from v$session order by logon_time desc";
			list = queryForList(con, sql);
			con.close();
			con = null;
		} catch (SQLException e) {
			System.err.println("SQLException: " + e);
			throw new Exception("连接数据库失败，原因：" + e.getMessage(), e);
		}
		return list;
	}

	public static void killSessions(DatabaseConnectionInfo info, String sid, String serial) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("驱动加载失败，原因：" + e.getMessage(), e);
		}
		try {
			Connection con = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
			String sql = "alter system kill session '" + sid + "," + serial + "'";
			System.out.println("强制断开连接:" + sql);
			Statement stmt = con.createStatement();
			stmt.execute(sql);
			stmt.close();
			con.close();
			con = null;
		} catch (SQLException e) {
			System.err.println("SQLException: " + e);
			throw new Exception("连接数据库失败，原因：" + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List queryForList(Connection con, String sql) throws SQLException {
		List list = new LinkedList();
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = ps.getMetaData();
		// 取得结果集列数
		int columnCount = rsmd.getColumnCount();
		Map data = null;
		while (rs.next()) {
			data = new HashMap<String, Object>();
			// 每循环一条将列名和列值存入Map
			for (int i = 1; i <= columnCount; i++) {
				data.put(rsmd.getColumnLabel(i), rs.getObject(rsmd.getColumnLabel(i)));
			}
			// 将整条数据的Map存入到List中
			list.add(data);
		}
		return list;
	}

	public static double getDbOneFieldSize(Connection conn, String sql) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != rs) {
					rs.close();
					rs = null;
				}
				if (null != pstmt) {
					pstmt.close();
					pstmt = null;
				}
			} catch (SQLException e) {
			}
		}
		return 0f;
	}

}
