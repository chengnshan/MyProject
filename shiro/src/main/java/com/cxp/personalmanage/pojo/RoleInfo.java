package com.cxp.personalmanage.pojo;

import java.io.Serializable;

public class RoleInfo extends BaseEntityInfo implements Serializable{

	private static final long serialVersionUID = 3206208596229553207L;
	
	private Integer id;
	private String roleId;
	private String roleName;
	private String description;
	private int enable;

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "RoleInfo [id=" + id + ", roleId=" + roleId + ", roleName=" + roleName + ", description=" + description
				+ ", create_time=" + create_time + ", create_user=" + create_user + ", update_time=" + update_time
				+ ", update_user=" + update_user + "]";
	}

}
