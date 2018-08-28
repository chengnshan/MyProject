package com.cxp.personalmanage.mapper;

import java.util.List;

import com.cxp.personalmanage.pojo.RoleInfo;
import com.cxp.personalmanage.pojo.UserInfo;

public interface RoleInfoMapper {

	public List<RoleInfo> findUserRoleInfoList(String userName);

	/**
	 * 查询角色信息
	 * @param roleInfo
	 * @return
	 */
	public List<RoleInfo> findRoleList(RoleInfo roleInfo);
}
