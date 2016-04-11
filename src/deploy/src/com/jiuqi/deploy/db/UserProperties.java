package com.jiuqi.deploy.db;

import java.util.ArrayList;
import java.util.List;

import com.jiuqi.deploy.util.StringHelper;

public class UserProperties {

	private String userName;
	private String password;
	private String defaultTablespace;

	private String tempTablespace;

	private List<String> privileges;
	private List<String> revokeprivilegs;

	public static final String PRIV_CONNECT = "CONNECT";
	public static final String PRIV_RESOURCE = "RESOURCE";
	public static final String PRIV_DBA = "DBA";

	public UserProperties() {
		privileges = new ArrayList<String>();
		revokeprivilegs = new ArrayList<String>();
	}

	public List<String> getPrivileges() {
		return privileges;
	}

	public List<String> getRevokeprivilegs() {
		return revokeprivilegs;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDefaultTablespace() {
		return defaultTablespace;
	}

	public void setDefaultTablespace(String defaultTablespace) {
		this.defaultTablespace = defaultTablespace;
	}

	public void setTempTablespace(String tempTablespace) {
		this.tempTablespace = tempTablespace;
	}

	public String getTempTablespace() {
		return tempTablespace;
	}

	public void addprivileges(String privilege) {
		privileges.add(privilege);
	}

	public void addRevokeprivilegs(String privilege) {
		revokeprivilegs.add(privilege);
	}

	public String buildCreateUserSQL() {
		StringBuilder sb = new StringBuilder();
		sb.append(" create user ").append(userName).append(" identified by ").append(password);
		if (!StringHelper.isEmpty(defaultTablespace)) {
			sb.append(" default tablespace ").append(defaultTablespace);
		}
		if (!StringHelper.isEmpty(tempTablespace)) {
			sb.append(" temporary tablespace ").append(tempTablespace);
		}
		return sb.toString();
	}

	public String buildGrantSQL() {
		StringBuilder sb = new StringBuilder();
		sb.append(" grant ");
		int i = 0;
		for (; i < privileges.size() - 1; i++) {
			sb.append(privileges.get(i)).append(",");
		}
		sb.append(privileges.get(i));
		sb.append(" to ").append(userName);
		return sb.toString();
	}

	public String buildRevokeGrantSQL() {
		StringBuilder sb = new StringBuilder();
		sb.append(" revoke ");
		int i = 0;
		for (; i < revokeprivilegs.size() - 1; i++) {
			sb.append(revokeprivilegs.get(i)).append(",");
		}
		sb.append(revokeprivilegs.get(i));
		sb.append(" from ").append(userName);
		return sb.toString();
	}

	public String buildModifyUserSQL() {
		StringBuilder sb = new StringBuilder();
		sb.append(" alter user ").append(userName);
		if (!StringHelper.isEmpty(password)) {
			sb.append(" identified by ").append(password);
		}
		if (!StringHelper.isEmpty(defaultTablespace)) {
			sb.append(" default tablespace ").append(defaultTablespace);
		}
		if (!StringHelper.isEmpty(tempTablespace)) {
			sb.append(" temporary tablespace ").append(tempTablespace);
		}
		return sb.toString();
	}
}

