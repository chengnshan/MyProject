package com.cxp.personalmanage.service;

import java.util.List;

import com.cxp.personalmanage.pojo.RoleInfo;

public interface RoleInfoService {

	/**
	 * 根据用户名获取所拥有的角色
	 * @param userName
	 * @return
	 */
	public List<RoleInfo> findUserRoleInfoList(String userName);

	/**
	 * 查询角色信息
	 * @param roleInfo
	 * @return
	 */
	public List<RoleInfo> findRoleList(RoleInfo roleInfo);
}
