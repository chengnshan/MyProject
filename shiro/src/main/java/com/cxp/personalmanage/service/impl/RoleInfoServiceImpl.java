package com.cxp.personalmanage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cxp.personalmanage.mapper.RoleInfoMapper;
import com.cxp.personalmanage.pojo.RoleInfo;
import com.cxp.personalmanage.service.RoleInfoService;

@Service(value="roleInfoService")
public class RoleInfoServiceImpl implements RoleInfoService {
	
	@Autowired
	private RoleInfoMapper roleInfoMapper;

	@Override
	public List<RoleInfo> findUserRoleInfoList(String userName) {
		return roleInfoMapper.findUserRoleInfoList(userName);
	}

	@Override
	public List<RoleInfo> findRoleList(RoleInfo roleInfo) {
		return roleInfoMapper.findRoleList(roleInfo);
	}
}
