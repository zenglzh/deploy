package com.jiuqi.deploy.server;

import java.util.HashMap;
import java.util.Map;

public class DistNodeType {

	static final public DistNodeType APP = new DistNodeType("app", "应用节点");
	static final public DistNodeType PARAM = new DistNodeType("param", "参数节点");
	static final public DistNodeType SSO = new DistNodeType("sso", "登录节点");
	static final public DistNodeType APPCLUSTER = new DistNodeType("appcluster", "应用集群节点");

	final static public Map<String, DistNodeType> map = new HashMap<String, DistNodeType>();

	static {
		map.put("app", APP);
		map.put("param", PARAM);
		map.put("sso", SSO);
		map.put("appcluster", APPCLUSTER);
	}

	public static DistNodeType getDistNodeByCode(String code) {
		return map.get(code);
	}

	private String code;
	private String title;


	public DistNodeType(String code, String title) {
		this.code = code;
		this.title = title;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		return getTitle();
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
