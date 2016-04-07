package com.jiuqi.deplay.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.jiuqi.deplay.server.Contants;

public class DBTools {
	
	//SQL语句
	public static String SQL_CREATE_TABLE = "CREATE TABLE " + Contants.CONFIG_TABLE_NAME + "("
											+ Contants.CONFIG_ORDER_FIELD + " INT, " + Contants.CONFIG_CONTENT_FIELD + " CHAR(200), " + Contants.CONFIG_CONTEXTPATH_FIELD + " VARCHAR2(200))";	
	public static String SQL_SELECT_TABLE = "SELECT * FROM " + Contants.CONFIG_TABLE_NAME + " WHERE " + Contants.CONFIG_CONTEXTPATH_FIELD + " = ? ";
	public static String SQL_DROP_TABLE = "DROP TABLE " + Contants.CONFIG_TABLE_NAME;
	public static String SQL_INSERT_TABLE = "INSERT INTO " + Contants.CONFIG_TABLE_NAME + " VALUES(?,?,?)";
	public static String SQL_DELETE_TABLE = "DELETE FROM " + Contants.CONFIG_TABLE_NAME + " WHERE " + Contants.CONFIG_CONTEXTPATH_FIELD + " = ? ";
	public static String SQL_CHECK_EXISTS = "SELECT COUNT(" + Contants.CONFIG_CONTEXTPATH_FIELD + ") FROM " + Contants.CONFIG_TABLE_NAME;
	
	/**
	 * 读取数据表信息
	 * @param conn 数据库连接
	 * @param contextPath 上下文路径
	 * @return String数组{str[0]为dna_server.xml的字符串、str[1]为dna根目录、str[2]为启动参数(可能为空)}
	 * @throws SQLException
	 */
	
	public static String[] readConfigInfo(Connection conn, String contextPath) throws SQLException{
		String[] configInfos = new String[3];
		PreparedStatement pst = conn.prepareStatement(SQL_SELECT_TABLE);
		pst.setString(1, contextPath);
		ResultSet rs = pst.executeQuery();
		Map<Integer,String> map = new TreeMap<Integer,String>( );
		while(rs.next()){
			map.put(rs.getInt(1), rs.getString(2));
		}
		rs.close();
		sortMap(map);
		Iterator<String> it = map.values().iterator();
		String longStr = "";
		while(it.hasNext()){
			longStr += it.next();
		}
		if(!"".equals(longStr)){
			String[] tempStr = longStr.split(Contants.STR_SEP);
			for(int i = 0;i<tempStr.length;i++){
				configInfos[i] = tempStr[i];
			}	
		}
		return configInfos;
	}
	
	public static void sortMap(Map<Integer,String> map){
		List<Map.Entry<Integer, String>> entryList = new ArrayList<Map.Entry<Integer, String>>(map.entrySet()); 
		Collections.sort(entryList, new Comparator<Map.Entry<Integer, String>>(){
			public int compare(Entry<Integer, String> o1,
					Entry<Integer, String> o2) {
				return o2.getKey()-o1.getKey();
			}
		});
	}
	
	
	public static void storeConfigInfo(Connection conn , String longStr, String contextPath) throws SQLException{
		try{
			deleteTable(conn, contextPath);
		}catch(SQLException e) {
			dropTable(conn);
			createTable(conn);
		}
		int length = longStr.length();
		double a = length;
		double b = Contants.STR_SIZE;
		int recordCount = (int)Math.ceil(a/b);
		for (int i = 0, order = 1; i < length; i += Contants.STR_SIZE, order++) {
			String tempStr = longStr.substring(i, order==recordCount? length:i + Contants.STR_SIZE);
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
	
	public static void createTable(Connection conn) throws SQLException{
		conn.prepareStatement(SQL_CREATE_TABLE).execute();
	}
	
	public static void dropTable(Connection conn) throws SQLException{
		conn.prepareStatement(SQL_DROP_TABLE).execute();
	}
	
	public static void deleteTable(Connection conn, String contextPath) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(SQL_DELETE_TABLE);
		pst.setString(1, contextPath);
		pst.execute();
	}
}
