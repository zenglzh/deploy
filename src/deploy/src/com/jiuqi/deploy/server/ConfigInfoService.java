package com.jiuqi.deploy.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;

import com.jiuqi.deploy.db.DBTools;
import com.jiuqi.deploy.db.ESBDBClient;
import com.jiuqi.deploy.util.XmlUtils;

public class ConfigInfoService {
	private String dbName;
	private Connection conn;
	private ESBDBClient dbclient;

	public ConfigInfoService(String dbName) throws Exception {
		this.dbName = dbName;
		Context initContext = new InitialContext();
		try {
			DataSource ds = (DataSource) initContext.lookup(this.dbName);// 这里根据配置更改
		} catch (NameNotFoundException e) {
			DataSource ds = (DataSource) ((Context) initContext.lookup("java:comp/env"))
					.lookup(dbName);// 这里根据配置更改
		}
	}
	
	public ConfigInfoService(ESBDBClient dbclient, String dbName) {
		this.dbclient = dbclient;
		this.dbName = dbName;
	}

	public void createConfigTable() {
		try {
			DBTools.createTable(conn);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void dropConfigTable() {
		try {
			DBTools.dropTable(conn);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public boolean checkConfigTableExists() {
		try {
			DBTools.checkExists(conn);
		} catch (SQLException e) {
			return true;
		}
		return false;
	}
	
	public ConfigEntity readConfigEntity(String contextPath) throws Exception {
		ConfigEntity entity = new ConfigEntity();
		String[] configArr = DBTools.readConfigInfo(conn, contextPath);
		String[] distConfig = DBTools.readConfigInfo(conn, Contants.DIST_PREFX+contextPath);
		if(configArr[0]!=null){
			transferFromXML(configArr[0],entity);
			entity.setRootPath(configArr[1]);
			if(configArr[2]!=null){
				entity.setSetupParam(configArr[2]);
			}	
		}
		if(null != distConfig[0]) {
			distTransferFromXML(distConfig[0],entity);
		}
		return entity;
	}

	
	public void storeConfigEntity(ConfigEntity entity) throws JDOMException,
			IOException, SQLException {
		// xml dom文件生成
		XMLOutputter xmlOut = new XMLOutputter();
		Document doc = new Document();
		Element dnaRoot = new Element(Contants.ELEMENT_DNA);
		doc.setRootElement(dnaRoot);
		storeDBInfo(dnaRoot);
		stroreSessionInfo(dnaRoot, entity);
		stroreNtlmInfo(dnaRoot, entity);
		stroreLdapInfo(dnaRoot, entity);
		storeClusterInfo(dnaRoot, entity);
		// 数据库保存
		ByteArrayOutputStream byteRsp = new ByteArrayOutputStream();
		xmlOut.output(doc, byteRsp);
		String longStr = byteRsp.toString() + Contants.STR_SEP + entity.getRootPath() + 
							Contants.STR_SEP + entity.getSetupParam();
		DBTools.storeConfigInfo(conn, longStr, entity.getContextPath());
	}
	
	/**
	 * 节点的分布式信息生成xml dom文档存入数据库
	 * @param entity
	 * @throws JDOMException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void storeDistConfigEntity(ConfigEntity entity) throws JDOMException,
	IOException, SQLException {
		if (null == entity.getDistNodes() || entity.getDistNodes().isEmpty()) {
			DBTools.deleteTable(conn, Contants.DIST_PREFX+entity.getContextPath());
			return ;
		}
		// xml dom文件生成
		XMLOutputter xmlOut = new XMLOutputter();
		Document doc = new Document();
		Element distRoot = new Element(Contants.ELEMENT_DISTRIBUTED);
		doc.setRootElement(distRoot);
		storeDistributedInfo(distRoot, entity);
		// 数据库保存
		ByteArrayOutputStream byteRsp = new ByteArrayOutputStream();
		xmlOut.output(doc, byteRsp);
		DBTools.storeConfigInfo(conn, byteRsp.toString(), Contants.DIST_PREFX+entity.getContextPath());
	}
	
	private void storeDBInfo(Element dnaRoot) {
		Element databasesElement = XmlUtils.safeGetChild(dnaRoot,
				Contants.ELEMENT_DATASOURCES);
		Element databaseElement = XmlUtils.safeGetChild(databasesElement,
				Contants.ELEMENT_DATASOURCE);
		XmlUtils.setValue(databaseElement, Contants.ATTRIBUTE_DB_NAME,this.dbName);
		XmlUtils.setValue(databaseElement, Contants.ATTRIBUTE_DB_LOCATION,this.dbName);
	}

	//	
	private void stroreLdapInfo(Element dnaRoot, ConfigEntity entity) {
		Element ldapElement = XmlUtils.safeGetChild(dnaRoot,
				Contants.ELEMENT_LDAP);
		XmlUtils.setValue(ldapElement, Contants.ATTRIBUTE_ENABLE, entity
				.getLdapEnable());
		XmlUtils.setValue(ldapElement, Contants.ATTRIBUTE_FACTORY_INITIAL,
				entity.getFactoryInitial());
		XmlUtils.setValue(ldapElement, Contants.ATTRIBUTE_PROVIDER_URL, entity
				.getProviderUrl());
		XmlUtils.setValue(ldapElement, Contants.ATTRIBUTE_LDAP_AUTHENTICATION,
				entity.getAuthentication());
		XmlUtils.setValue(ldapElement, Contants.ATTRIBUTE_LDAP_DOMAIN, entity
				.getLdapDomain());
		String entrys = entity.getIncludeEntrys();
		if (entrys != null && !"".equals(entrys)) {
			Element includeElement = XmlUtils.safeGetChild(ldapElement,
					Contants.ELEMENT_INCLUDE);
			for (String s : entrys.split(",")) {
				Element e = XmlUtils.safeGetChild(includeElement,
						Contants.ELEMENT_ENTRY);
				e.setText(s);
			}
		}
	}
	
	private void storeClusterInfo(Element dnaRoot, ConfigEntity entity) {
		Element clusterElement = XmlUtils.safeGetChild(dnaRoot,
				Contants.ELEMENT_CLUSTER);
		XmlUtils.setValue(clusterElement, Contants.ATTRIBUTE_ENABLE, entity
				.getClusterEnable());
		XmlUtils.setValue(clusterElement, Contants.ATTRIBUTE_CLUSTER_ID, entity
				.getClusterID());
		XmlUtils.setValue(clusterElement, Contants.ATTRIBUTE_CLUSTER_INDEX, entity
				.getClusterIndex());
		
		for(String nodeUrl : entity.getNodes()) {
			Element nodeElement = XmlUtils.safeGetChild(clusterElement, Contants.ELEMENT_NODE);
			XmlUtils.setValue(nodeElement, Contants.ATTRIBUTE_NODE_URL, nodeUrl);
		}
		
	}
	
	private void storeDistributedInfo(Element distRoot, ConfigEntity entity) {
		Element distElement = distRoot; 
		XmlUtils.setValue(distElement, Contants.ATTRIBUTE_ENABLE, entity
				.getDistEnable());
		Map<String, Element> clusters = new HashMap<String, Element>();
		for(ConfigDistEntity nodeInfo : entity.getDistNodes()) {
			
			Element nodeElement = null;
			String nodeType = nodeInfo.getNodeType().toUpperCase().trim();
			if(ConfigDistEntity.NodeType.SSO.name().equals(nodeType)) {
				nodeElement = XmlUtils.safeGetChild(distElement, Contants.ELEMENT_SSO);
			}else if(ConfigDistEntity.NodeType.APPCLUSTER.name().equals(nodeType)) {
				Element	clusterEL = clusters.get(nodeInfo.getNodeClusterId());
				if(null == clusterEL) {
					clusterEL = XmlUtils.safeGetChild(distElement, Contants.ELEMENT_CLUSTER);
					clusters.put(nodeInfo.getNodeClusterId(), clusterEL);
					XmlUtils.setValue(clusterEL, Contants.ATTRIBUTE_CLUSTER_ID, nodeInfo.getNodeClusterId());
				}
				nodeElement = XmlUtils.safeGetChild(clusterEL, Contants.ELEMENT_NODE);
				if(null != nodeInfo.getNodeDispatchRule() && !"".equals(nodeInfo.getNodeDispatchRule().trim())) {
					clusterEL.removeChild(Contants.ELEMENT_DISPATCH_RULE);
					Element dispatchEL = XmlUtils.safeGetChild(clusterEL, Contants.ELEMENT_DISPATCH_RULE);
					XmlUtils.setValue(dispatchEL, Contants.ATTRIBUTE_DISTRIBUTED_TEMPLATE, Contants.ATTRIBUTE_TEMPLATE_VALUE);
					dispatchEL.addContent(new CDATA(nodeInfo.getNodeDispatchRule()));
				}
			} else {
				nodeElement = XmlUtils.safeGetChild(distElement, Contants.ELEMENT_NODE);
				if(null != nodeInfo.getNodeDispatchRule() && !"".equals(nodeInfo.getNodeDispatchRule().trim())) {
					Element dispatchEL = XmlUtils.safeGetChild(nodeElement, Contants.ELEMENT_DISPATCH_RULE);
					XmlUtils.setValue(dispatchEL, Contants.ATTRIBUTE_DISTRIBUTED_TEMPLATE, Contants.ATTRIBUTE_TEMPLATE_VALUE);
					dispatchEL.addContent(new CDATA(nodeInfo.getNodeDispatchRule()));
				}
			}
			
			if (null != nodeInfo.getCurrentNode()
					&& Contants.ATTRIBUTE_TRUE_VALUE.equals(nodeInfo.getCurrentNode().trim())) {
				XmlUtils.setValue(nodeElement, Contants.ATTRIBUTE_NODE_SELF, nodeInfo.getCurrentNode().trim());
				if(ConfigDistEntity.NodeType.PARAM.name().equals(nodeType)) {
					XmlUtils.setValue(distElement, Contants.ATTRIBUTE_DISTRIBUTED_TYPE, ConfigDistEntity.NodeType.APP.name().toLowerCase());
					XmlUtils.setValue(distElement, Contants.ATTRIBUTE_DISTRIBUTED_PARAM, Contants.ATTRIBUTE_TRUE_VALUE);
				} else if(ConfigDistEntity.NodeType.SSO.name().equals(nodeType)) {
					XmlUtils.setValue(distElement, Contants.ATTRIBUTE_DISTRIBUTED_TYPE, ConfigDistEntity.NodeType.SSO.name().toLowerCase());
					XmlUtils.setValue(distElement, Contants.ATTRIBUTE_DISTRIBUTED_AUTO_DISPATCH, Contants.ATTRIBUTE_TRUE_VALUE);
				}else {
					XmlUtils.setValue(distElement, Contants.ATTRIBUTE_DISTRIBUTED_TYPE, ConfigDistEntity.NodeType.APP.name().toLowerCase());
				}
			}
			
			XmlUtils.setValue(nodeElement, Contants.ATTRIBUTE_DISTRIBUTED_TYPE, nodeInfo.getNodeType());
			XmlUtils.setValue(nodeElement, Contants.ELEMENT_ID, nodeInfo.getNodeId());
			XmlUtils.setValue(nodeElement, Contants.ATTRIBUTE_HOST, nodeInfo.getNodeHost());
			XmlUtils.setValue(nodeElement, Contants.ATTRIBUTE_PORT, nodeInfo.getNodePort());
			XmlUtils.setValue(nodeElement, Contants.ATTRIBUTE_CONTEXT_PATH, nodeInfo.getNodeContextPath());
		}
	}
	
	private void stroreNtlmInfo(Element dnaRoot, ConfigEntity entity) {
		Element ntmlElement = XmlUtils.safeGetChild(dnaRoot,
				Contants.ELEMENT_NTLM);
		String enable = entity.getNtlmEnable();
		String userName = entity.getNtlmUser();
		String pwd = entity.getNtlmPwd();
		String entrys = entity.getExcludeEntrys();
		String controller = entity.getDomainController();
		XmlUtils.setValue(ntmlElement, Contants.ATTRIBUTE_ENABLE, enable);
		if (userName != null) {
			Element userElement = XmlUtils.safeGetChild(ntmlElement,
					Contants.ELEMENT_NTLM_USER);
			userElement.setText(userName);
		}
		if (pwd != null) {
			Element pwdElement = XmlUtils.safeGetChild(ntmlElement,
					Contants.ELEMENT_NTLM_PWD);
			pwdElement.setText(pwd);
		}
		if (entrys != null && !"".equals(entrys)) {
			Element excludeElement = XmlUtils.safeGetChild(ntmlElement,
					Contants.ELEMENT_EXCLUDE);
			for (String s : entrys.split(",")) {
				Element temp = XmlUtils.safeGetChild(excludeElement,
						Contants.ELEMENT_ENTRY);
				temp.setText(s);
			}
		}
		if (controller != null) {
			Element controlElement = XmlUtils.safeGetChild(ntmlElement,
					Contants.ELEMENT_DOMAIN_CONTROLLER);
			controlElement.setText(controller);
		}
	}

	private void stroreSessionInfo(Element dnaRoot, ConfigEntity entity) {
		Element sessionElement = XmlUtils.safeGetChild(dnaRoot,
				Contants.ELEMENT_SESSION);
		XmlUtils.setValue(sessionElement, Contants.ATTRIBUTE_HEART_BEAT_S,
				entity.getSessionHeartBeat());
		XmlUtils.setValue(sessionElement, Contants.ATTRIBUTE_TIMEOUT_M, entity
				.getSessionTimeout());
	}

	private void transferFromXML(String str, ConfigEntity entity)
			throws JDOMException, IOException, SQLException {
		InputStream in = new ByteArrayInputStream(str.getBytes());
		Element dnaRoot = XmlUtils.readXml(in);
		fillSessionInfo(dnaRoot, entity);
		fillNtlmInfo(dnaRoot, entity);
		fillLdapInfo(dnaRoot, entity);
		fillClusterInfo(dnaRoot, entity);
		in.close();
	}
	
	private void distTransferFromXML(String str, ConfigEntity entity) 
			throws JDOMException, IOException, SQLException {
		InputStream in = new ByteArrayInputStream(str.getBytes());
		Element distributedRoot = XmlUtils.readXml(in);
		entity.setDistEnable(distributedRoot
				.getAttributeValue(Contants.ATTRIBUTE_ENABLE));
		List<Element> nodes = distributedRoot.getChildren(Contants.ELEMENT_NODE);
		List<Element> clusters = distributedRoot.getChildren(Contants.ELEMENT_CLUSTER);
		Element ssoElement = distributedRoot.getChild(Contants.ELEMENT_SSO);
		
		List<ConfigDistEntity> distNodes = new ArrayList<ConfigDistEntity>();
		
		for(Element node : nodes) {
			ConfigDistEntity distNode = createDistEntity(node, null, null);
			distNodes.add(distNode);
		}
		
		if(null != clusters) {
			for(Element cluster : clusters) {
				Element dispatchRule = cluster.getChild(Contants.ELEMENT_DISPATCH_RULE);
				String clusterID = cluster.getAttributeValue(Contants.ATTRIBUTE_CLUSTER_ID);
				List<Element> cnodes = cluster.getChildren(Contants.ELEMENT_NODE);
				if(null != cnodes) {
					for(Element node : cnodes) {
						ConfigDistEntity distNode = createDistEntity(node, dispatchRule, clusterID);
						distNodes.add(distNode);
					}
				}
			}
		}
		
		if(null != ssoElement) {
			ConfigDistEntity distSSONode = new ConfigDistEntity();
			distSSONode.setNodeId(ssoElement.getAttributeValue(Contants.ELEMENT_ID));
			distSSONode.setNodeHost(ssoElement.getAttributeValue(Contants.ATTRIBUTE_HOST));
			distSSONode.setNodePort(ssoElement.getAttributeValue(Contants.ATTRIBUTE_PORT));
			distSSONode.setNodeType(ssoElement.getAttributeValue(Contants.ATTRIBUTE_DISTRIBUTED_TYPE));
			distSSONode.setNodeContextPath(ssoElement.getAttributeValue(Contants.ATTRIBUTE_CONTEXT_PATH));
			if(Contants.ATTRIBUTE_TRUE_VALUE.equals(ssoElement.getAttributeValue(Contants.ATTRIBUTE_NODE_SELF))) {
				distSSONode.setCurrentNode(Contants.ATTRIBUTE_TRUE_VALUE);
			}
			distNodes.add(distSSONode);
		}
		entity.setDistNodes(distNodes);
		in.close();
	}
	
	private ConfigDistEntity createDistEntity(Element node, Element distpatchRuleEL, String clusterID) {
		ConfigDistEntity distNode = new ConfigDistEntity();
		if(null != node.getChild(Contants.ELEMENT_DISPATCH_RULE)) {
			Element dispatchRule = node.getChild(Contants.ELEMENT_DISPATCH_RULE);
			distNode.setNodeDispatchRule(dispatchRule.getText());
		} else if(null != distpatchRuleEL) {
			distNode.setNodeDispatchRule(distpatchRuleEL.getText());
		}
		distNode.setNodeId(node.getAttributeValue(Contants.ELEMENT_ID));
		distNode.setNodeHost(node.getAttributeValue(Contants.ATTRIBUTE_HOST));
		distNode.setNodePort(node.getAttributeValue(Contants.ATTRIBUTE_PORT));
		distNode.setNodeType(node.getAttributeValue(Contants.ATTRIBUTE_DISTRIBUTED_TYPE));
		distNode.setNodeContextPath(node.getAttributeValue(Contants.ATTRIBUTE_CONTEXT_PATH));
		if(null != clusterID) {
			distNode.setNodeClusterId(clusterID);
		}
		if(Contants.ATTRIBUTE_TRUE_VALUE.equals(node.getAttributeValue(Contants.ATTRIBUTE_NODE_SELF))) {
			distNode.setCurrentNode(Contants.ATTRIBUTE_TRUE_VALUE);
		}
		return distNode;
	}
	
	private void fillClusterInfo(Element dnaRoot, ConfigEntity entity) {
		//集群信息
		Element clusterElement = dnaRoot.getChild(Contants.ELEMENT_CLUSTER);
		if(null != clusterElement) {
			entity.setClusterEnable(clusterElement.getAttributeValue(Contants.ATTRIBUTE_ENABLE));
			entity.setClusterID(clusterElement.getAttributeValue(Contants.ATTRIBUTE_CLUSTER_ID));
			entity.setClusterIndex(clusterElement.getAttributeValue(Contants.ATTRIBUTE_CLUSTER_INDEX));
			
			List<Element> nodes = clusterElement.getChildren(Contants.ELEMENT_NODE);
			List<String> nodesUrl = new ArrayList<String>();
			for(Element e : nodes) {
				nodesUrl.add(e.getAttributeValue(Contants.ATTRIBUTE_NODE_URL));
			}
			entity.setNodes(nodesUrl);
		}
	}
	
	private void fillSessionInfo(Element dnaRoot, ConfigEntity entity) {
		// 会话信息
		Element sessionElement = dnaRoot.getChild(Contants.ELEMENT_SESSION);
		if (sessionElement != null) {
			entity.setSessionHeartBeat(sessionElement
					.getAttributeValue(Contants.ATTRIBUTE_HEART_BEAT_S));
			entity.setSessionTimeout(sessionElement
					.getAttributeValue(Contants.ATTRIBUTE_TIMEOUT_M));
		}
	}

	private void fillNtlmInfo(Element dnaRoot, ConfigEntity entity) {
		// ntlm信息
		Element ntlmElement = dnaRoot.getChild(Contants.ELEMENT_NTLM);
		if (ntlmElement == null) {
			return;
		}
		entity.setNtlmEnable(ntlmElement
				.getAttributeValue(Contants.ATTRIBUTE_ENABLE));
		Element domainControlElement = ntlmElement
				.getChild(Contants.ELEMENT_DOMAIN_CONTROLLER);
		entity.setDomainController(domainControlElement == null ? null
				: domainControlElement.getText());
		Element userElement = ntlmElement.getChild(Contants.ELEMENT_NTLM_USER);
		entity.setNtlmUser(userElement == null ? null : userElement.getText());
		Element pwdElement = ntlmElement.getChild(Contants.ELEMENT_NTLM_PWD);
		entity.setNtlmPwd(pwdElement == null ? null : pwdElement.getText());
		Element entrys = ntlmElement.getChild(Contants.ELEMENT_EXCLUDE);
		if (entrys != null) {
			List<Element> excludeEntryElements = entrys.getChildren();
			String ntlmExcludeEntrys = "";
			for (Element e : excludeEntryElements) {
				ntlmExcludeEntrys += e.getText() + ",";
			}
			String str = ntlmExcludeEntrys.endsWith(",") ? ntlmExcludeEntrys
					.substring(0, ntlmExcludeEntrys.length() - 1)
					: ntlmExcludeEntrys;
			entity.setExcludeEntrys(str);
		}
	}

	private void fillLdapInfo(Element dnaRoot, ConfigEntity entity) {
		// ldap信息
		Element ldapElement = dnaRoot.getChild(Contants.ELEMENT_LDAP);
		if (ldapElement == null) {
			return;
		}
		entity.setLdapDomain(ldapElement
				.getAttributeValue(Contants.ATTRIBUTE_LDAP_DOMAIN));
		entity.setLdapEnable(ldapElement
				.getAttributeValue(Contants.ATTRIBUTE_ENABLE));
		entity.setFactoryInitial(ldapElement
				.getAttributeValue(Contants.ATTRIBUTE_FACTORY_INITIAL));
		entity.setAuthentication(ldapElement
				.getAttributeValue(Contants.ATTRIBUTE_LDAP_AUTHENTICATION));
		entity.setProviderUrl(ldapElement
				.getAttributeValue(Contants.ATTRIBUTE_PROVIDER_URL));
		Element entrys = ldapElement.getChild(Contants.ELEMENT_INCLUDE);
		if (entrys != null) {
			List<Element> includeEntryElements = entrys.getChildren();
			String ldapIncludeEntrys = "";
			for (Element e : includeEntryElements) {
				ldapIncludeEntrys += e.getText() + ",";
			}
			String str = ldapIncludeEntrys.endsWith(",") ? ldapIncludeEntrys
					.substring(0, ldapIncludeEntrys.length() - 1)
					: ldapIncludeEntrys;
			entity.setIncludeEntrys(str);
		}
	}

	public void openConnect() throws Exception {
		dbclient.connect();
		conn = dbclient.getConn();
	}

	public void closeConnect() {
		try {
			if (conn != null) {
				dbclient.disconnect();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
