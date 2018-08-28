package com.cxp.personalmanage.pojo.excel;

import com.cxp.personalmanage.annotation.ExcelVOAttribute;

public class ExcelUserInfo {

	@ExcelVOAttribute(name = "用户名", column = "A")
	private String userName;// 用户名
	@ExcelVOAttribute(name = "真实姓名", column = "B")
	private String realName;
	@ExcelVOAttribute(name = "密码", column = "C")
	private String password;// 密码
	@ExcelVOAttribute(name = "盐", column = "D")
	private String salt;// 盐
	@ExcelVOAttribute(name = "是否可用", column = "E")
	private String enable;
	@ExcelVOAttribute(name = "角色Id", column = "F")
	private String roleId;
	@ExcelVOAttribute(name = "角色名称", column = "G")
	private String roleName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
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

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
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

	@Override
	public String toString() {
		return "ExcelUserInfo [userName=" + userName + ", realName=" + realName + ", password=" + password + ", salt="
				+ salt + ", enable=" + enable + ", roleId=" + roleId + ", roleName=" + roleName + "]";
	}

}
