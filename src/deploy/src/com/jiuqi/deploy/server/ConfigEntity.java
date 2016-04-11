package com.jiuqi.deploy.server;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ConfigEntity {

	// 会话
	private String sessionHeartBeat;
	private String sessionTimeout;

	// ntlm
	private String ntlmEnable;
	private String domainController;
	private String ntlmUser;
	private String ntlmPwd;
	private String excludeEntrys;

	// ldap
	private String ldapEnable;
	private String factoryInitial;
	private String providerUrl;
	private String authentication;
	private String ldapDomain;
	private String includeEntrys;
	
	//集群参数
	private String clusterEnable;
	private String clusterID;
	private String clusterIndex;
	private List<String> nodes;
	
	//分布式参数
	private String distEnable;
	private List<ConfigDistEntity> distNodes;
	
	// 其他参数
	private String rootPath;
	private String setupParam;
	private String contextPath;
	
	public String getSessionHeartBeat() {
		return sessionHeartBeat;
	}

	public void setSessionHeartBeat(String sessionHeartBeat) {
		this.sessionHeartBeat = sessionHeartBeat;
	}

	public String getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(String sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public String getNtlmEnable() {
		return ntlmEnable;
	}

	public void setNtlmEnable(String ntlmEnable) {
		this.ntlmEnable = ntlmEnable;
	}

	public String getDomainController() {
		return domainController;
	}

	public void setDomainController(String domainController) {
		this.domainController = domainController;
	}

	public String getNtlmUser() {
		return ntlmUser;
	}

	public void setNtlmUser(String ntlmUser) {
		this.ntlmUser = ntlmUser;
	}

	public String getNtlmPwd() {
		return ntlmPwd;
	}

	public void setNtlmPwd(String ntlmPwd) {
		this.ntlmPwd = ntlmPwd;
	}

	public String getExcludeEntrys() {
		return excludeEntrys;
	}

	public void setExcludeEntrys(String excludeEntrys) {
		this.excludeEntrys = excludeEntrys;
	}

	public String getLdapEnable() {
		return ldapEnable;
	}

	public void setLdapEnable(String ldapEnable) {
		this.ldapEnable = ldapEnable;
	}

	public String getFactoryInitial() {
		return factoryInitial;
	}

	public void setFactoryInitial(String factoryInitial) {
		this.factoryInitial = factoryInitial;
	}

	public String getProviderUrl() {
		return providerUrl;
	}

	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String getLdapDomain() {
		return ldapDomain;
	}

	public void setLdapDomain(String ldapDomain) {
		this.ldapDomain = ldapDomain;
	}

	public String getIncludeEntrys() {
		return includeEntrys;
	}

	public void setIncludeEntrys(String includeEntrys) {
		this.includeEntrys = includeEntrys;
	}
	
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	
	public String getSetupParam() {
		return setupParam;
	}

	public void setSetupParam(String setupParam) {
		this.setupParam = setupParam;
	}
	
	public String getClusterID() {
		return clusterID;
	}

	public void setClusterID(String clusterID) {
		this.clusterID = clusterID;
	}

	public String getClusterIndex() {
		return clusterIndex;
	}

	public void setClusterIndex(String clusterIndex) {
		this.clusterIndex = clusterIndex;
	}

	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}
	
	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	public String getClusterEnable() {
		return clusterEnable;
	}

	public void setClusterEnable(String clusterEnable) {
		this.clusterEnable = clusterEnable;
	}
	
	public String getDistEnable() {
		return distEnable;
	}

	public void setDistEnable(String distEnable) {
		this.distEnable = distEnable;
	}

	public List<ConfigDistEntity> getDistNodes() {
		return distNodes;
	}

	public void setDistNodes(List<ConfigDistEntity> distNodes) {
		this.distNodes = distNodes;
	}

	public JSONObject transferToJson() {
		JSONObject obj = new JSONObject();
		obj.put("sessionHeartBeat", getSafeValue(sessionHeartBeat));
		obj.put("sessionTimeout", getSafeValue(sessionTimeout));
		
		obj.put("ntlmEnable", getSafeValue(ntlmEnable));
		obj.put("domainController", getSafeValue(domainController));
		obj.put("ntlmUser", getSafeValue(ntlmUser));
		obj.put("ntlmPwd", getSafeValue(ntlmPwd));
		obj.put("excludeEntrys", getSafeValue(excludeEntrys));
		
		obj.put("ldapEnable", getSafeValue(ldapEnable));
		obj.put("factoryInitial", getSafeValue(factoryInitial));
		obj.put("authentication", getSafeValue(authentication));
		obj.put("ldapDomain", getSafeValue(ldapDomain));
		obj.put("providerUrl", getSafeValue(providerUrl));
		obj.put("includeEntrys", getSafeValue(includeEntrys));
		
		obj.put("clusterEnable", getSafeValue(clusterEnable));
		obj.put("clusterID", getSafeValue(clusterID));
		obj.put("clusterIndex", getSafeValue(clusterIndex));
		obj.put("nodes", getNodesUrl());
		
		obj.put("distEnable", getSafeValue(distEnable));
		obj.put("distNodes", getDistNodesInfo());
		
		obj.put("rootPath", getSafeValue(rootPath));
		obj.put("setupParam", getSafeValue(setupParam));
		obj.put("contextPath", getSafeValue(contextPath));
		return obj;
	}
	
	private String getSafeValue(String value) {
		return value == null ? "" : value;
	}
	
	public String getNodesUrl() {
		StringBuilder nodesResult = new StringBuilder();
		if(null != nodes) {
			for(String nodeUrl : nodes) {
				nodesResult.append(nodeUrl+"\n");
			}
		}
		return nodesResult.toString();
	}
	
	private String getDistNodesInfo() {
		JSONArray jsonArray = new JSONArray();
		if(null != distNodes) {
			for(ConfigDistEntity distEntity : distNodes) {
				jsonArray.add(distEntity.transformToJSON());
			}
		}
		return jsonArray.toString();
	}
}
