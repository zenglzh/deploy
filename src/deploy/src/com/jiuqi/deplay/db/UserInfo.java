package com.jiuqi.deplay.db;

import java.util.List;

public class UserInfo {

	private String username;
	private String password;
	private String defaultTablespace;
	private String temp_Tablespace;
	private List<String> roles;
	public static final String SHOW_PASSWORD = "****************";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDefaultTablespace() {
		return defaultTablespace;
	}

	public void setDefaultTablespace(String defaultTablespace) {
		this.defaultTablespace = defaultTablespace;
	}

	public String getTemp_Tablespace() {
		return temp_Tablespace;
	}

	public void setTemp_Tablespace(String temp_Tablespace) {
		this.temp_Tablespace = temp_Tablespace;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return username;
	}

	public void setRolePries(List<String> roles) {
		this.roles = roles;
	}

	public List<String> getRolePries() {
		return this.roles;
	}

	public boolean hasRole(String role) {
		if (null != role) {
			for (String string : roles) {
				if (string.equalsIgnoreCase(role)) {
					return true;
				}
			}
		}
		return false;

	}
}
