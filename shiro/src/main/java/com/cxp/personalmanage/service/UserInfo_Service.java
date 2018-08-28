package com.cxp.personalmanage.service;

import java.util.List;

import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.pojo.excel.ExcelUserInfo;

public interface UserInfo_Service {

	/**
	 * 删除用户
	 * @param userName  用户名
	 * @return
	 */
    public int deleteUserInfoAndRole(String userName);
    
	/**
	 *  添加用户和角色
	 * @param addUserName 用户名
	 * @param addRealName	真实姓名
	 * @param addRoleId	角色
	 * @param addUserPassword 密码
	 * @return
	 * @throws Exception
	 */
    public int saveUserInfoAndRole(String addUserName, String addRealName, String addRoleId, String addUserPassword) throws Exception;
    
    /**
     * 更新用户
     * @param userInfo
     * @return
     */
    public int updateUserAndRole(UserInfo userInfo) throws Exception;
    
    /**
     * 
     * @param userInfo
     * @return
     */
    public List<ExcelUserInfo> exportUserInfo(UserInfo userInfo) throws Exception;
    
    /**
     * 上传用户
     * @param excelList
     * @return
     * @throws Exception
     */
    public String importUserInfo(List<ExcelUserInfo> excelList) throws Exception;
}
