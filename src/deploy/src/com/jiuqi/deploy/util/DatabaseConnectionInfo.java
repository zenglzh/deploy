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
	 * �Ǽ�Ⱥ��
	 * <pre>
	 *    url=jdbc:oracle:thin:@host_ip:1521:dbname
	 * </pre> 
	 * ��Ⱥ
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
				//�����Լ��������׷�Ӽ�Ⱥ��Ϣ����ʽ����//bf.append("(address=(protocol=tcp)(host=10.37.27.112)(port=1521))");
				url.append("(load_balance=yes)"); 
				//load_balance=yes;��ʾ�Ƿ��ؾ���url.append(")");//address_list ���� 
				url.append("(connect_data =");
				url.append("(server = dedicated)");//server = dedicated;��ʾר�÷�����ģʽ������ȥ��url.append("(service_name=wangjj)");
				//���ݿ��������url.append("(failover_mode =")��url.append("(type=session)"); 
				//TYPE = SESSION��ʾ��һ�����ӺõĻỰ��ʵ���������ϣ�ϵͳ���Զ����Ự�л����������õ�ʵ����ǰ̨Ӧ�������ٶȷ������ӣ����Ự����ִ�е�SQL ��Ҫ����ִ��url.append("(method=basic)");
				//METHOD = BASIC��ʾ��ʼ���Ӿ�����һ���ӵ㣬�����и�ѡ����preconnect����ʾ��ʼ�������еĽӵ�url.append("(retries=5)");
				//RETRIES ���Դ���url.append("(delay=15)")��
				//DELAY �����ӳ� ��λΪ��url.append��"��"����  
				//failover_mode ����url.append��"��"����  
				//connect_data ����url.append��"��"���� //description ����
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
