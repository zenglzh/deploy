package com.jiuqi.deploy.util;

/**
 *
 * @author esalaza
 */
public class DatabaseConnectionInfo {
    
    private String host     = null;
    private String port     = null;
    private String username = null;
    private String password = null;
    private String sid      = null;
    private int    id       = 0;
	private String connectID = null;
	public static String[] CONNECTID = { "Normal", "SYSDBA" };
	public static int DBConnectPro = 1001;

	public String getUrl() {
		return "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;// url
	}
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

	public String getConnectID() {
		if(null==connectID){
			return CONNECTID[0];
		}
		return connectID;
	}

	public boolean isDBA() {
		return getConnectID().equals(CONNECTID[1]);
	}
	public void setConnectID(String connectID) {
		this.connectID = connectID;
	}

	@Override
	public String toString() {
		return username + "@" + host + ":" + port + ":" + sid;// url
	}
}
