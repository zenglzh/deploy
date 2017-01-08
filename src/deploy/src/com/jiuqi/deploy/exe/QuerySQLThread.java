package com.jiuqi.deploy.exe;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.server.MulTableModelData;

public class QuerySQLThread implements Runnable {

	private ESBDBClient dbClient;
	private String query;
	private Vector parameters;
	private MulTableModelData head;
	private String provinceCode;

	public QuerySQLThread() {

	}

	@Override
	public void run() {
		if (head.hasSQLSyntaxError()) {
			return;
		}
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
			if (!head.inited()) {
				buildHeadData(head, rset, cols);
			}
			buildBodyData(head, rset);
		} catch (SQLSyntaxErrorException e) {
			head.setException(e);
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
	}

	private void buildHeadData(MulTableModelData head, ResultSet rset, int cols) throws SQLException {
		head.append("CODE", String.class, 100);
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

	private void buildBodyData(MulTableModelData head, ResultSet rset) throws SQLException {
		int j = 0;
		while (rset.next() && j < 1) {
			Vector<Object> row = new Vector<Object>();
			row.add(provinceCode);
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
