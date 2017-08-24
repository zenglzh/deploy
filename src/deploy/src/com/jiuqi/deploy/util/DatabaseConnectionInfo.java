package com.jiuqi.deploy.util;

/**
 *
 * @author esalaza
 */
public class DatabaseConnectionInfo {
    private String datasource = null;
    private String host     = null;
    private String port     = null;
    private String username = null;
    private String password = null;
    private String sid      = null;
    private int    id       = 0;
    private String url;
	private String connectID = null;
	public static String[] CONNECTID = { "Normal", "SYSDBA" };
	public static int DBConnectPro = 1001;

	
	/**
	 * 非集群：
	 * <pre>
	 *    url=jdbc:oracle:thin:@host_ip:1521:dbname
	 * </pre> 
	 * 集群
	 * <pre>
	 *    url=jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST =db1)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST =db2)(PORT = 1521))(LOAD_BALANCE=yes)(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = dbname)))
	 * </pre>
	 * @return
	 */
	public String getUrl() {
		if(null == url)
			return "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;// url
		else {
			return url;
		}
	}
	
	public boolean hasUrl(){
		return null!=url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	

	public String getDatasource() {
		return datasource;
	}
	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}
	
	private void _cluserUrl(){
		StringBuffer url = new StringBuffer(); 
				url.append("jdbc:oracle:thin:@(description= (address_list =");
				url.append("(address=(protocol=tcp)(host=192.168.31.9)(port=1521))");
				//根据自己情况继续追加集群信息，格式如下//bf.append("(address=(protocol=tcp)(host=10.37.27.112)(port=1521))");
				url.append("(load_balance=yes)"); 
				//load_balance=yes;表示是否负载均衡url.append(")");//address_list 结束 
				url.append("(connect_data =");
				url.append("(server = dedicated)");//server = dedicated;表示专用服务器模式，可以去掉url.append("(service_name=wangjj)");
				//数据库服务名称url.append("(failover_mode =")；url.append("(type=session)"); 
				//TYPE = SESSION表示当一个连接好的会话的实例发生故障，系统会自动将会话切换到其他可用的实例，前台应用无须再度发起连接，但会话正在执行的SQL 需要重新执行url.append("(method=basic)");
				//METHOD = BASIC表示初始连接就连接一个接点，彵还有个选项是preconnect，表示初始连接所有的接点url.append("(retries=5)");
				//RETRIES 重试次数url.append("(delay=15)")；
				//DELAY 重试延迟 单位为秒url.append（"）"）；  
				//failover_mode 结束url.append（"）"）；  
				//connect_data 结束url.append（"）"）； //description 结束
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
