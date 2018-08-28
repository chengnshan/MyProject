package com.cxp.personalmanage.service.impl;

import java.util.List;
import java.util.Map;

import com.cxp.personalmanage.mapper.UserRoleInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cxp.personalmanage.mapper.UserInfoMapper;
import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.service.UserInfoService;

@Service(value = "userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;
	@Autowired
	private UserRoleInfoMapper userRoleInfoMapper;

	@Override
	public List<UserInfo> findUserInfoList(Map<String, Object> param) {
		return userInfoMapper.findUserInfoList(param);
	}

	@Override
	public Integer findUserInfoListCount(Map<String, Object> param) {
		return userInfoMapper.findUserInfoListCount(param);
	}

	@Override
	public UserInfo getUserInfo(UserInfo userInfo) {
		return userInfoMapper.getUserInfo(userInfo);
	}

    @Override
	public UserInfo getUserInfoByUserName(String userName) {
		// TODO Auto-generated method stub
		return userInfoMapper.getUserInfoByUserName(userName);
	}

	@Override
    public int bathSaveUserInfo(List<UserInfo> list) {
        return userInfoMapper.bathSaveUserInfo(list);
    }

    @Override
    public int saveUserInfo(UserInfo userInfo) {
        int result = userInfoMapper.saveUserInfo(userInfo);
        return result;
    }

    @Override
    public int updateUserInfo(UserInfo userInfo) {
        return userInfoMapper.updateUserInfo(userInfo);
    }

}
