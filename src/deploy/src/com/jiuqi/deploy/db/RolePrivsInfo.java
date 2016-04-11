package com.jiuqi.deploy.db;

import java.util.List;

public class RolePrivsInfo {

	private String grantee;
	private List<String> grantee_role;

	public String getGrantee() {
		return grantee;
	}

	public void setGrantee(String grantee) {
		this.grantee = grantee;
	}

	public void setGrantee_role(List<String> grantee_role) {
		this.grantee_role = grantee_role;
	}

	public List<String> getGrantee_role() {
		return grantee_role;
	}
}
