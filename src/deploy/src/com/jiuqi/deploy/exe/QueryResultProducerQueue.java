package com.jiuqi.deploy.exe;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

import com.jiuqi.deploy.db.ArchiveMonitorDBInfo;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.intf.IProduct;
import com.jiuqi.deploy.server.TableBody;
import com.jiuqi.deploy.server.TableHeader;
import com.jiuqi.deploy.util.IMonitor;

public class QueryResultProducerQueue extends Thread {

	private BlockTable bTable;
	private ArchiveMonitorDBInfo dbInfo;
	private String query;
	private Vector<Object> parameters;
	private ESBDBClient dbClient;
	private IMonitor monitor;

	public QueryResultProducerQueue(BlockTable bTable, ArchiveMonitorDBInfo dbInfo, String query, Vector<Object> parameters, IMonitor monitor) {
		this.bTable = bTable;
		this.dbInfo = dbInfo;
		this.query = query;
		this.parameters = parameters;
		this.dbClient = new ESBDBClient(dbInfo.getConnect());
		this.monitor = monitor;
		bTable.put(dbInfo.getProvinceCode(), new TableBody());
	}

	@Override
	public void run() {
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
			synchronized (bTable) {
				buildHeadData(rset, cols);
			}
			buildBodyData(rset);
		} catch (SQLSyntaxErrorException e) {
			monitor.error("SQL Óï·¨´íÎó£º" + e.getMessage());
			interrupt();
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

	private void buildHeadData(ResultSet rset, int cols) throws SQLException {
		if (bTable.initedHeader()) {
			return;
		}
		TableHeader head = bTable.getHeader();
		head.append("__CODE__", String.class, 100);
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
		bTable.buildTableModel();
	}

	private void buildEmptyBodyData() {
		IProduct product = bTable.get(dbInfo.getProvinceCode());
		try {
			bTable.putToQueue(product);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void buildBodyData(ResultSet rset) throws SQLException, InterruptedException {
		int j = 0;
		IProduct product = bTable.get(dbInfo.getProvinceCode());
		Vector<Object> row = product.getRow();
		row.clear();
		row.add(dbInfo.getProvinceCode());
		while (rset.next() && j < 1) {
			for (int i = 0; i < rset.getMetaData().getColumnCount(); i++)
				try {
					row.add(rset.getObject(i + 1));
				} catch (Throwable ex) {
					row.add(null);
					ex.printStackTrace();
				}
			j++;
		}
		bTable.putToQueue(product);
	}

	private String getColumnClassName(ResultSet rset, int colIndex) {
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
