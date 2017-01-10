package com.jiuqi.deploy.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jsqltool.model.CustomTableModel;

import com.jiuqi.deploy.exception.QueryException;
import com.jiuqi.deploy.server.Contants;
import com.jiuqi.deploy.server.MulTableModelData;
import com.jiuqi.deploy.util.DatabaseConnectionInfo;

public class DBTools {

	// SQL语句
	public static String SQL_CREATE_TABLE = "CREATE TABLE " + Contants.CONFIG_TABLE_NAME + "(" + Contants.CONFIG_ORDER_FIELD + " INT, " + Contants.CONFIG_CONTENT_FIELD + " CHAR(200), "
			+ Contants.CONFIG_CONTEXTPATH_FIELD + " VARCHAR2(200))";
	public static String SQL_SELECT_TABLE = "SELECT * FROM " + Contants.CONFIG_TABLE_NAME + " WHERE " + Contants.CONFIG_CONTEXTPATH_FIELD + " = ? ";
	public static String SQL_DROP_TABLE = "DROP TABLE " + Contants.CONFIG_TABLE_NAME;
	public static String SQL_INSERT_TABLE = "INSERT INTO " + Contants.CONFIG_TABLE_NAME + " VALUES(?,?,?)";
	public static String SQL_DELETE_TABLE = "DELETE FROM " + Contants.CONFIG_TABLE_NAME + " WHERE " + Contants.CONFIG_CONTEXTPATH_FIELD + " = ? ";
	public static String SQL_CHECK_EXISTS = "SELECT COUNT(" + Contants.CONFIG_CONTEXTPATH_FIELD + ") FROM " + Contants.CONFIG_TABLE_NAME;

	/**
	 * 读取数据表信息
	 * 
	 * @param conn
	 *            数据库连接
	 * @param contextPath
	 *            上下文路径
	 * @return 
	 *         String数组{str[0]为dna_server.xml的字符串、str[1]为dna根目录、str[2]为启动参数(可能为空)
	 *         }
	 * @throws SQLException
	 */

	public static String[] readConfigInfo(Connection conn, String contextPath) throws SQLException {
		String[] configInfos = new String[3];
		PreparedStatement pst = conn.prepareStatement(SQL_SELECT_TABLE);
		pst.setString(1, contextPath);
		ResultSet rs = pst.executeQuery();
		Map<Integer, String> map = new TreeMap<Integer, String>();
		while (rs.next()) {
			map.put(rs.getInt(1), rs.getString(2));
		}
		rs.close();
		sortMap(map);
		Iterator<String> it = map.values().iterator();
		String longStr = "";
		while (it.hasNext()) {
			longStr += it.next();
		}
		if (!"".equals(longStr)) {
			String[] tempStr = longStr.split(Contants.STR_SEP);
			for (int i = 0; i < tempStr.length; i++) {
				configInfos[i] = tempStr[i];
			}
		}
		return configInfos;
	}

	public static void sortMap(Map<Integer, String> map) {
		List<Map.Entry<Integer, String>> entryList = new ArrayList<Map.Entry<Integer, String>>(map.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<Integer, String>>() {
			public int compare(Entry<Integer, String> o1, Entry<Integer, String> o2) {
				return o2.getKey() - o1.getKey();
			}
		});
	}

	public static void storeConfigInfo(Connection conn, String longStr, String contextPath) throws SQLException {
		try {
			deleteTable(conn, contextPath);
		} catch (SQLException e) {
			dropTable(conn);
			createTable(conn);
		}
		int length = longStr.length();
		double a = length;
		double b = Contants.STR_SIZE;
		int recordCount = (int) Math.ceil(a / b);
		for (int i = 0, order = 1; i < length; i += Contants.STR_SIZE, order++) {
			String tempStr = longStr.substring(i, order == recordCount ? length : i + Contants.STR_SIZE);
			PreparedStatement pst = conn.prepareStatement(SQL_INSERT_TABLE);
			pst.setInt(1, order);
			pst.setString(2, tempStr);
			pst.setString(3, contextPath);
			pst.execute();
			pst.close();
		}
	}

	public static void checkExists(Connection conn) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(SQL_CHECK_EXISTS);
		pst.execute();
	}

	public static void createTable(Connection conn) throws SQLException {
		conn.prepareStatement(SQL_CREATE_TABLE).execute();
	}

	public static void dropTable(Connection conn) throws SQLException {
		conn.prepareStatement(SQL_DROP_TABLE).execute();
	}

	public static void deleteTable(Connection conn, String contextPath) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(SQL_DELETE_TABLE);
		pst.setString(1, contextPath);
		pst.execute();
	}

	public static TableModel queryData(ESBDBClient dbClient, String query, Vector parameters) throws SQLSyntaxErrorException {
		MulTableModelData head = new MulTableModelData();
		ResultSet rset = null;
		Statement stmt = null;
		try {
			dbClient.connect();
			if (null == parameters || parameters.isEmpty()) {
				stmt = dbClient.getConn().createStatement();
				rset = stmt.executeQuery(query);
			} else {
				stmt = dbClient.getConn().prepareStatement(query);
				for (int i = 0; i < parameters.size(); i++) {
					((PreparedStatement) stmt).setObject(i + 1, parameters.get(i));
				}
				rset = ((PreparedStatement) stmt).executeQuery();
			}
			int cols = rset.getMetaData().getColumnCount() + 1;
			buildHeadData(head, rset, cols);
			buildBodyData(head, rset);
		} catch (SQLSyntaxErrorException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != rset) {
					rset.close();
				}
				if (null != stmt) {
					stmt.close();
				}
				dbClient.disconnect();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return head.buildTableModel();
	}

	public static TableModel queryAll(List<ArchiveMonitorDBInfo> monitorDBInfos, String query, Vector parameters) throws SQLSyntaxErrorException {
		if (null != monitorDBInfos && !monitorDBInfos.isEmpty()) {
			MulTableModelData head = new MulTableModelData();
			for (ArchiveMonitorDBInfo dbinfos : monitorDBInfos) {
				if (!dbinfos.isValid())
					continue;
				DatabaseConnectionInfo connectInfo = dbinfos.getConnect();
				ESBDBClient dbClient = new ESBDBClient(connectInfo);
				ResultSet rset = null;
				Statement stmt = null;
				try {
					dbClient.connect();
					if (null == parameters || parameters.isEmpty()) {
						stmt = dbClient.getConn().createStatement();
						rset = stmt.executeQuery(query);
					} else {
						stmt = dbClient.getConn().prepareStatement(query);
						for (int i = 0; i < parameters.size(); i++) {
							((PreparedStatement) stmt).setObject(i + 1, parameters.get(i));
						}
						rset = ((PreparedStatement) stmt).executeQuery();
					}
					int cols = rset.getMetaData().getColumnCount() + 1;
					buildHeadData(head, rset, cols);
					buildBodyData(head, dbinfos, rset);
				} catch (SQLSyntaxErrorException e) {
					throw e;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					break;
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (null != rset) {
							rset.close();
						}
						if (null != stmt) {
							stmt.close();
						}
						dbClient.disconnect();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return new DefaultTableModel();
	}

	private static void buildBodyData(MulTableModelData head, ResultSet rset) throws SQLException {
		int j = 0;
		while (rset.next() && j < 100) {
			Vector<Object> row = new Vector<Object>();
			row.add(j + 1);
			for (int i = 0; i < rset.getMetaData().getColumnCount(); i++)
				try {
					row.add(rset.getObject(i + 1));
				} catch (Throwable ex) {
					row.add(null);
					ex.printStackTrace();
				}
			head.appendRow(row);
			j++;
		}
	}

	private static void buildBodyData(MulTableModelData head, ArchiveMonitorDBInfo dbinfos, ResultSet rset) throws SQLException {
		int j = 0;
		while (rset.next() && j < 1) {
			Vector<Object> row = new Vector<Object>();
			row.add(dbinfos.getProvinceCode());
			for (int i = 0; i < rset.getMetaData().getColumnCount(); i++)
				try {
					row.add(rset.getObject(i + 1));
				} catch (Throwable ex) {
					row.add(null);
					ex.printStackTrace();
				}
			head.appendRow(row);
			j++;
		}
	}

	private static void buildHeadData(MulTableModelData head, ResultSet rset, int cols) throws SQLException {
		head.append(" ");
		head.append(25);
		head.append(Integer.class);
		String className = null;
		for (int i = 0; i < cols - 1; i++) {
			boolean isBlob = false;
			String colName = rset.getMetaData().getColumnName(i + 1);
			head.append(colName);
			className = getColumnClassName(rset, i + 1);
			if (className == null)
				className = "java.lang.String";
			else if (className.equals("byte[]") || className.equals("oracle.sql.BLOB")) {
				className = "java.sql.Blob";
				isBlob = true;
			}
			Class<?> forName = null;
			try {
				forName = Class.forName(className);
			} catch (Exception e) {
				forName = String.class;
			}
			head.append(forName);
			try {
				if (isBlob) {
					head.append(150);
				} else {
					head.append(Math.min((rset.getMetaData().getPrecision(i + 1) == 0 ? // case//
																						// MySQL...
					Math.max(rset.getMetaData().getColumnDisplaySize(i + 1) * 10, colName.length() * 10)
							: Math.max(rset.getMetaData().getPrecision(i + 1) * 10, colName.length() * 10)), Math.max(200, colName.length() * 10)));
				}
			} catch (SQLException e) {
				head.append(colName.length() * 10);
			}
		}
		head.buildTableModel();
	}

	private static ResultSet query(Connection conn, ResultSet rset, Statement stmt, String query, Vector parameters) {
		try {
			if (null == parameters || parameters.isEmpty()) {
				stmt = conn.createStatement();
				rset = stmt.executeQuery(query);
			} else {
				stmt = conn.prepareStatement(query);
				for (int i = 0; i < parameters.size(); i++) {
					((PreparedStatement) stmt).setObject(i + 1, parameters.get(i));
				}
				rset = ((PreparedStatement) stmt).executeQuery();
			}
		} catch (Exception e) {
		}
		return rset;
	}

	/**
	 * @param query
	 *            query to execute
	 * @param startPos
	 *            first record to read
	 * @param maxRows
	 *            max number of records to read
	 * @return table model which contains the records
	 */
	public synchronized TableModel getQuery(Connection conn, String query, Vector parameters, int startPos, int maxRows) throws QueryException {
		Statement stmt = null;
		ResultSet rset = null;
		try {
			if (parameters.size() == 0) {
				stmt = conn.createStatement();
				rset = stmt.executeQuery(query);
			} else {
				stmt = conn.prepareStatement(query);
				for (int i = 0; i < parameters.size(); i++)
					((PreparedStatement) stmt).setObject(i + 1, parameters.get(i));
				rset = ((PreparedStatement) stmt).executeQuery();
			}
			Vector data = new Vector();
			String className = null;
			String[] colNames = new String[rset.getMetaData().getColumnCount()];
			Class[] classTypes = new Class[rset.getMetaData().getColumnCount()];
			int[] colSizes = new int[rset.getMetaData().getColumnCount()];
			for (int i = 0; i < rset.getMetaData().getColumnCount(); i++) {
				boolean isBlob = false;
				colNames[i] = rset.getMetaData().getColumnName(i + 1);
				try {
					className = getColumnClassName(rset, i + 1);
					if (className == null)
						className = "java.lang.String";
					else if (className.equals("byte[]") || className.equals("oracle.sql.BLOB")) {
						className = "java.sql.Blob";
						isBlob = true;
					}
					classTypes[i] = Class.forName(className);
				} catch (NullPointerException ex) {
					classTypes[i] = String.class;
				}
				try {
					if (isBlob)
						colSizes[i] = 150;
					else
						colSizes[i] = Math.min((rset.getMetaData().getPrecision(i + 1) == 0 ? // case
																								// MySQL...
						Math.max(rset.getMetaData().getColumnDisplaySize(i + 1) * 10, colNames[i].length() * 10)
								: Math.max(rset.getMetaData().getPrecision(i + 1) * 10, colNames[i].length() * 10)), Math.max(200, colNames[i].length() * 10));
				} catch (SQLException ex1) {
					colSizes[i] = colNames[i].length() * 10;
				}
			}
			CustomTableModel model = new CustomTableModel(colNames, classTypes, colSizes);
			int j = 0;
			if (startPos > 0)
				while (rset.next() && j < startPos)
					j++;
			j = 0;
			Vector row = null;
			while (rset.next() && j < maxRows) {
				row = new Vector();
				for (int i = 0; i < rset.getMetaData().getColumnCount(); i++)
					try {
						row.add(rset.getObject(i + 1));
					} catch (Throwable ex) {
						row.add(null);
						ex.printStackTrace();
					}
				j++;
				data.add(row);
				// model.addRow(row);
			}
			model.setDataVector(data);
			model.setEditMode(model.DETAIL_REC);
			return model;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new QueryException(ex.getMessage());
			// JOptionPane.showMessageDialog(
			// parent,
			// Options.getInstance().getResource(
			// "error while executing query")
			// + ":\n" + ex.getMessage(), Options.getInstance()
			// .getResource("error"), JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				rset.close();
			} catch (Exception ex2) {
			}
			try {
				stmt.close();
			} catch (Exception ex3) {
			}
		}
	}

	/**
	 * Some JDBC Drivers don't support JDBC method: in that case java class type
	 * is derived by java.sql.Types
	 */
	private static String getColumnClassName(ResultSet rset, int colIndex) {
		try {
			return rset.getMetaData().getColumnClassName(colIndex);
		} catch (SQLException ex) {
			try {
				int colType = rset.getMetaData().getColumnType(colIndex);
				if (colType == Types.BIGINT || colType == Types.INTEGER || colType == Types.SMALLINT || colType == Types.TINYINT)
					return "java.lang.Integer";
				if (colType == Types.BINARY || colType == Types.BLOB || colType == Types.LONGVARBINARY)
					return "java.sql.Blob";
				if (colType == Types.BIT || colType == Types.BOOLEAN)
					return "java.lang.Boolean";
				if (colType == Types.CLOB)
					return "java.sql.Clob";
				if (colType == Types.DATE || colType == Types.TIME || colType == Types.TIMESTAMP)
					return "java.sql.Timestamp";
				if (colType == Types.DECIMAL || colType == Types.DOUBLE || colType == Types.FLOAT || colType == Types.NUMERIC || colType == Types.REAL)
					return "java.math.BigDecimal";
			} catch (SQLException ex1) {
			}
			return "java.lang.String";
		}
	}
}
