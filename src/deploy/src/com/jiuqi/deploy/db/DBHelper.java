package com.jiuqi.deploy.db;

public class DBHelper {

	private static DBHelper dbinstance = new DBHelper();

	private String dbhost;
	private int dbport;
	private String database;
	private String user;
	private String pass;

	private DBHelper() {

	}

	public DBHelper getInstance() {
		return dbinstance;
	}

}
