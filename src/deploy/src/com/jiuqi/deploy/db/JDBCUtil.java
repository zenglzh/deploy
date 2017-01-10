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
			throw new Exception("��������ʧ�ܣ�ԭ��" + e.getMessage(), e);
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
			throw new Exception("�������ݿ�ʧ�ܣ�ԭ��" + e.getMessage(), e);
		}
		return result;
	}

	public static void createTablespace(String conn_alias, String ts_name) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("��������ʧ�ܣ�ԭ��" + e.getMessage(), e);
		}

		try {
			DatabaseConnectionInfo info = null;// DBConfig.getConnInfoByAlias(conn_alias);
			Connection con = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
			String createTableSpace = "CREATE TABLESPACE " + ts_name + " LOGGING DATAFILE '" + ts_name
					+ ".DBF' SIZE 100M AUTOEXTEND ON NEXT 200M MAXSIZE 20480M EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO";
			System.out.println("������ռ�:" + createTableSpace);
			Statement stmt = con.createStatement();
			stmt.execute(createTableSpace);

			stmt.close();
			con.close();
		} catch (SQLException e) {
			System.err.println("SQLException: " + e);
			throw new Exception("������ռ�ʧ�ܣ�ԭ��" + e.getMessage(), e);
		}
	}

	public static void createUser(String conn_alias, String username, String password, String tablespace) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("��������ʧ�ܣ�ԭ��" + e.getMessage(), e);
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
			throw new Exception("�����û�ʧ�ܣ�ԭ��" + e.getMessage(), e);
		}
	}

	public static String exportDump(DatabaseConnectionInfo info, String filePath) throws Exception {
		if (null == info) {
			throw new Exception("���ݿ�δ���ӡ�");
		}
		try {
			String cmdStr = "exp " + info.getUsername() + "/" + info.getPassword() + "@//" + info.getHost() + ":" + info.getPort() + "/" + info.getSid() + " file=\"" + filePath + "\" ";
			System.out.println(cmdStr);
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(cmdStr);
			String line = null;
			StringBuffer buffer = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream(), "GBK"));
			// ��ȡErrorStream�ܹؼ����������˹�������⡣
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
			// throw new Exception("����ʧ��");
			// }
			return buffer.toString();

		} catch (Exception e) {
			System.err.println("SQLException: " + e);
			throw new Exception("����ʧ�ܣ�ԭ��" + e.getMessage(), e);
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
			// ��ȡErrorStream�ܹؼ����������˹�������⡣
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
			throw new Exception("����ʧ�ܣ�ԭ��" + e.getMessage(), e);
		}
	}

	public static List querySessions(DatabaseConnectionInfo info) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("��������ʧ�ܣ�ԭ��" + e.getMessage(), e);
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
			throw new Exception("�������ݿ�ʧ�ܣ�ԭ��" + e.getMessage(), e);
		}
		return list;
	}

	public static void killSessions(DatabaseConnectionInfo info, String sid, String serial) throws Exception {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException: " + e);
			throw new Exception("��������ʧ�ܣ�ԭ��" + e.getMessage(), e);
		}
		try {
			Connection con = DriverManager.getConnection(info.getUrl(), info.getUsername(), info.getPassword());
			String sql = "alter system kill session '" + sid + "," + serial + "'";
			System.out.println("ǿ�ƶϿ�����:" + sql);
			Statement stmt = con.createStatement();
			stmt.execute(sql);
			stmt.close();
			con.close();
			con = null;
		} catch (SQLException e) {
			System.err.println("SQLException: " + e);
			throw new Exception("�������ݿ�ʧ�ܣ�ԭ��" + e.getMessage(), e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List queryForList(Connection con, String sql) throws SQLException {
		List list = new LinkedList();
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = ps.getMetaData();
		// ȡ�ý��������
		int columnCount = rsmd.getColumnCount();
		Map data = null;
		while (rs.next()) {
			data = new HashMap<String, Object>();
			// ÿѭ��һ������������ֵ����Map
			for (int i = 1; i <= columnCount; i++) {
				data.put(rsmd.getColumnLabel(i), rs.getObject(rsmd.getColumnLabel(i)));
			}
			// ���������ݵ�Map���뵽List��
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
