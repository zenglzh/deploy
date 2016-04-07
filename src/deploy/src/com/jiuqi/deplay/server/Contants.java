package com.jiuqi.deplay.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * 配置文件里的常量
 * @author wjuan
 *
 */
public class Contants {
	//数据源默认前缀
	public static String DB_PREFIX = "DNA_DS";
	//分布式前缀
	public static String DIST_PREFX = "DIST_";
	//LDAP登录类型
	public static String[] LDAP_LOGIN_TYPE = {"simple","strong","none"}; 
	
	//配置信息数据库名称、字段名、记录名
	public static String CONFIG_TABLE_NAME = "DNA_CONFIG_INFO";
	public static String CONFIG_ORDER_FIELD = "CONFIG_PROPERTY";
	public static String CONFIG_CONTENT_FIELD = "CONFIG_CONTENT";
	public static String CONFIG_CONTEXTPATH_FIELD = "CONFIG_CONTEXTPATH";
	
	public static int STR_SIZE = 200;
	public static String STR_SEP = "#####";
	
	// 分布式节点类型
	public static final Map<String, String> DIST_NODETYPE = new TreeMap<String, String>();
	public static final List<DistNodeType> DIST_NODES = new ArrayList<DistNodeType>(
			Arrays.asList(DistNodeType.APP, DistNodeType.PARAM, DistNodeType.SSO, DistNodeType.APPCLUSTER));

	//=========================dna-server.xml=========================//
	public static final String ELEMENT_ID = "id";
	public static final String ELEMENT_DNA = "dna";
	public static final String ELEMENT_NTLM = "ntlm";
	public static final String ELEMENT_LDAP= "ldap";
	public static final String ELEMENT_LISTEN = "listen";
	public static final String ELEMENT_SESSION = "session";
	public static final String ELEMENT_DOMAIN_CONTROLLER = "domain_controller";
	public static final String ELEMENT_NTLM_USER = "user";
	public static final String ELEMENT_NTLM_PWD = "password";
	public static final String ELEMENT_EXCLUDE = "exclude";
	public static final String ELEMENT_INCLUDE = "include";
	public static final String ELEMENT_ENTRY = "entry";
	public static final String ELEMENT_DATASOURCES = "datasources";
	public static final String ELEMENT_DATASOURCE = "datasource";
	public static final String ELEMENT_CLUSTER = "cluster";
	public static final String ELEMENT_NODE = "node";
	public static final String ELEMENT_DISTRIBUTED = "distributed"; 
	public static final String ELEMENT_SSO  = "sso";
	public static final String ELEMENT_DISPATCH_RULE = "dispatch_rule";
	
	public static final String ATTRIBUTE_TIMEOUT_M = "timeout-m";
	public static final String ATTRIBUTE_HEART_BEAT_S = "heartbeat-s";
	public static final String ATTRIBUTE_DATA_SOURCE = "datasource";
	public static final String ATTRIBUTE_SPACE = "space";
	public static final String ATTRIBUTE_INDEX = "index";
	public static final String ATTRIBUTE_ENABLE = "enable";
	public static final String ATTRIBUTE_FACTORY_INITIAL = "factory-initial";
	public static final String ATTRIBUTE_PROVIDER_URL = "provider-url";
	public static final String ATTRIBUTE_LDAP_DOMAIN = "domain";
	public static final String ATTRIBUTE_LDAP_AUTHENTICATION = "authentication";
	public static final String ATTRIBUTE_DB_NAME = "name";
	public static final String ATTRIBUTE_DB_LOCATION = "location";
	public static final String ATTRIBUTE_CLUSTER_ID = "id";
	public static final String ATTRIBUTE_CLUSTER_INDEX = "index";
	public static final String ATTRIBUTE_NODE_URL = "url";
	public static final String ATTRIBUTE_DISTRIBUTED_TYPE = "type";
	public static final String ATTRIBUTE_DISTRIBUTED_AUTO_DISPATCH = "auto_dispatch";
	public static final String ATTRIBUTE_DISTRIBUTED_PARAM = "param";
	public static final String ATTRIBUTE_DISTRIBUTED_TEMPLATE = "template";
	public static final String ATTRIBUTE_HOST = "host";
	public static final String ATTRIBUTE_PORT = "port";
	public static final String ATTRIBUTE_NODE_SELF = "self";
	public static final String ATTRIBUTE_TITLE = "title";
	public static final String ATTRIBUTE_CONTEXT_PATH = "contextPath";
	
	public static final String DEFAULT_SITE_NAME = "root";
	//================================================================//
	public static final String ATTRIBUTE_TEMPLATE_VALUE = "unit_root_stdcode";
	public static final String ATTRIBUTE_TRUE_VALUE = "true";
}
