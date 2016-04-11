package com.jiuqi.deploy.server;

import net.sf.json.JSONObject;

/**
 * 分布式配置节点信息实体
 * @author xiongshilin
 *
 */
public class ConfigDistEntity {
	
	private String currentNode;//当前节点
	private String nodeType;//节点类型
	private String nodeId;//节点标识
	private String nodeHost;//节点地址
	private String nodePort;//节点端口号
	private String nodeContextPath;//节点上下文根
	private String nodeDispatchRule; //分发规则
	private String nodeClusterId;//节点所属集群id
	
	public static final String[] FieldTitle = { "当前节点", "类型", "标识", "节点地址", "端口", "节点上下文", "分发规则", "所属集群ID" };
	public static final String[] FieldName = {};

	public Object getFieldValue(int index) {
		Object[] fileds = { Contants.ATTRIBUTE_ENABLE.equals(currentNode), DistNodeType.getDistNodeByCode(nodeType),
				nodeId, nodeHost,
				nodePort,
				nodeContextPath,
				nodeDispatchRule,
				nodeClusterId };
		if (index < fileds.length && index > -1) {
			return fileds[index];
		}
		return "";
	}

	public void setFieldValue(int index, Object object) {

	}

	public String getCurrentNode() {
		return currentNode;
	}
	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeHost() {
		return nodeHost;
	}
	public void setNodeHost(String nodeHost) {
		this.nodeHost = nodeHost;
	}
	public String getNodePort() {
		return nodePort;
	}
	public void setNodePort(String nodePort) {
		this.nodePort = nodePort;
	}
	public String getNodeDispatchRule() {
		return nodeDispatchRule;
	}
	public void setNodeDispatchRule(String nodeDispatchRule) {
		this.nodeDispatchRule = nodeDispatchRule;
	}
	public String getNodeContextPath() {
		return nodeContextPath;
	}
	public void setNodeContextPath(String nodeContextPath) {
		this.nodeContextPath = nodeContextPath;
	}
	public String getNodeClusterId() {
		return nodeClusterId;
	}
	public void setNodeClusterId(String nodeClusterId) {
		this.nodeClusterId = nodeClusterId;
	}
	public JSONObject transformToJSON() {
		JSONObject obj = new JSONObject();
		obj.put("currentNode", getSafeValue(currentNode));
		obj.put("nodeType", getSafeValue(nodeType));
		obj.put("nodeId", getSafeValue(nodeId));
		obj.put("nodeHost", getSafeValue(nodeHost));
		obj.put("nodePort", getSafeValue(nodePort));
		obj.put("nodeContextPath", getSafeValue(nodeContextPath));
		obj.put("nodeDispatchRule", getSafeValue(nodeDispatchRule));
		obj.put("nodeClusterId", getSafeValue(nodeClusterId));
		return obj;
	}
	
	private String getSafeValue(String value) {
		return value == null ? "" : value;
	}
	
	public enum NodeType {
		APP, SSO, PARAM, APPCLUSTER
	}
}
