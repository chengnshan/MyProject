package com.cxp.personalmanage.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserInfo extends BaseEntityInfo implements Serializable {
	private static final long serialVersionUID = -5890268832873153185L;
	private Integer id;
	private String userName;// 用户名
	private String password;// 密码
	private String salt;// 盐
	private String realName;

	private List<RoleInfo> roleList = new ArrayList<RoleInfo>();
	private int enable = 1;

	private String[] roleIds;

	public String[] getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public List<RoleInfo> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<RoleInfo> roleList) {
		this.roleList = roleList;
	}

	public int getEnable() {
		return enable;
	}

	public void setEnable(int enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", userName=" + userName + ", password=" + password + ", salt=" + salt
				+ ", realName=" + realName + ", roleList=" + roleList + ", create_time=" + create_time
				+ ", create_user=" + create_user + ", update_time=" + update_time + ", update_user=" + update_user
				+ "]";
	}

}
