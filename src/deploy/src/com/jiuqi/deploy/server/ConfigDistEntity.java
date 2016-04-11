package com.jiuqi.deploy.server;

import net.sf.json.JSONObject;

/**
 * �ֲ�ʽ���ýڵ���Ϣʵ��
 * @author xiongshilin
 *
 */
public class ConfigDistEntity {
	
	private String currentNode;//��ǰ�ڵ�
	private String nodeType;//�ڵ�����
	private String nodeId;//�ڵ��ʶ
	private String nodeHost;//�ڵ��ַ
	private String nodePort;//�ڵ�˿ں�
	private String nodeContextPath;//�ڵ������ĸ�
	private String nodeDispatchRule; //�ַ�����
	private String nodeClusterId;//�ڵ�������Ⱥid
	
	public static final String[] FieldTitle = { "��ǰ�ڵ�", "����", "��ʶ", "�ڵ��ַ", "�˿�", "�ڵ�������", "�ַ�����", "������ȺID" };
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
