package com.cxp.personalmanage.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cxp.personalmanage.common.Constant;
import com.cxp.personalmanage.mapper.UserRoleInfoMapper;
import com.cxp.personalmanage.pojo.RoleInfo;
import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.pojo.UserRoleInfo;
import com.cxp.personalmanage.pojo.excel.ExcelUserInfo;
import com.cxp.personalmanage.service.RoleInfoService;
import com.cxp.personalmanage.service.UserInfoService;
import com.cxp.personalmanage.service.UserInfo_Service;
import com.cxp.personalmanage.utils.CommonUtil;
import com.cxp.personalmanage.utils.JackJsonUtil;
import com.cxp.personalmanage.utils.MD5Util;

@Service("userInfo_ServiceImpl")
public class UserInfo_ServiceImpl implements UserInfo_Service {

	private static final Logger logger = LoggerFactory.getLogger(UserInfo_ServiceImpl.class);
			
    @Autowired
    private UserRoleInfoMapper userRoleInfoMapper;
    @Autowired
	private UserInfoService userInfoService;
    @Autowired
    private RoleInfoService roleInfoService;
    
    @Autowired
	@Qualifier(value = "stringRedisTemplate")
	private StringRedisTemplate stringRedisTemplate;

    /**
     * 删除用户
     */
    @Transactional(rollbackFor = {Exception.class,RuntimeException.class},propagation = Propagation.REQUIRED)
    @Override
    public int deleteUserInfoAndRole(String userName) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(userName);
        userInfo.setEnable(0);
        int updateUserInfoNum = userInfoService.updateUserInfo(userInfo);
        if(updateUserInfoNum <= 0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("用户删除失败.");
        }
        UserRoleInfo userRoleInfo = new UserRoleInfo();
        userRoleInfo.setUserName(userName);
        userRoleInfo.setEnable(0);
        int updateUserRoleInfoNum = userRoleInfoMapper.updateUserRoleInfo(userRoleInfo);
        if(updateUserRoleInfoNum <= 0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("用户角色删除失败.");
        }
        return 1;
    }
    
    @Transactional(rollbackFor = {Exception.class,RuntimeException.class},propagation = Propagation.REQUIRED)
    @Override
    public int saveUserInfoAndRole(String addUserName,String addRealName,String addRoleId,String addUserPassword) throws Exception{
        //获取当前操作的用户
  //  	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
 //       String ipAddr = CommonUtil.getIpAddr(request);
 //       String loginUserStr = stringRedisTemplate.opsForValue().get(ipAddr);
 //       UserInfo loginUser = JackJsonUtil.jsonToObject(loginUserStr,UserInfo.class);
    	// 获取当前的Subject
    	Subject currentUser = SecurityUtils.getSubject();
    	Object principal = currentUser.getPrincipal();
    	UserInfo loginUser = new UserInfo();
        try {
    		 BeanUtils.copyProperties(loginUser, principal);
    	 } catch (IllegalAccessException | InvocationTargetException e) {
    		 e.printStackTrace();
    	 }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(addUserName);
        UserInfo existsUserInfo = userInfoService.getUserInfo(userInfo);
        //如果不存在，则直接添加
        if(null == existsUserInfo) {
        	userInfo.setEnable(1);
        	userInfo.setRealName(addRealName);
            userInfo.setSalt(Constant.SALT);
            userInfo.setPassword(MD5Util.encryptM5("MD5", addUserPassword, Constant.SALT, 3));
            userInfo.setCreate_time(new Date());
            userInfo.setUpdate_time(new Date());
            userInfo.setCreate_user(loginUser.getUserName());
            userInfo.setUpdate_user(loginUser.getUserName());
            int saveUserNum = userInfoService.saveUserInfo(userInfo);
            if(saveUserNum <= 0){
                throw new RuntimeException("添加用户失败.");
            }
        }else {
        	//如果存在，则修改为可用状态
        	userInfo.setEnable(1);
        	userInfo.setRealName(addRealName);
        	userInfo.setSalt(Constant.SALT);
            userInfo.setPassword(MD5Util.encryptM5("MD5", addUserPassword, Constant.SALT, 3));
        	userInfo.setUpdate_time(new Date());
        	userInfo.setUpdate_user(loginUser.getUserName());
        	int saveUserNum = userInfoService.updateUserInfo(userInfo);
            if(saveUserNum <= 0){
                throw new RuntimeException("添加用户失败.");
            }
        }
        
        UserRoleInfo userRoleInfo=new UserRoleInfo();
        userRoleInfo.setUserName(addUserName);
        List<UserRoleInfo> existsUserRoleList = userRoleInfoMapper.findUserRoleByUserName(userRoleInfo);
        if(CollectionUtils.isNotEmpty(existsUserRoleList)) {
        	userRoleInfoMapper.deleteUserRoleInfo(addUserName);
        }
        List<UserRoleInfo> userRoleList= new ArrayList<>();
        String[] roleArray = addRoleId.split(",");
        for (String rarr : roleArray) {
        	userRoleInfo = new UserRoleInfo();
        	userRoleInfo.setRoleId(rarr);
            userRoleInfo.setUserName(addUserName);
            userRoleInfo.setCreate_user(loginUser.getUserName());
            userRoleInfo.setCreate_time(new Date());
            userRoleInfo.setUpdate_user(loginUser.getUserName());
            userRoleInfo.setUpdate_time(new Date());
            userRoleList.add(userRoleInfo);
		}
        
        int userRoleInfoNum = userRoleInfoMapper.batchInsertUserRole(userRoleList);
        if(userRoleInfoNum <= 0){
            throw new RuntimeException("用户绑定角色失败.");
        }
        return 1;
    }

    @Transactional(rollbackFor= {Exception.class,RuntimeException.class},propagation=Propagation.REQUIRED,isolation=Isolation.DEFAULT)
	@Override
	public int updateUserAndRole(UserInfo userInfo) throws Exception{
    	int userRoleNum = 0;
    	
 /*   	HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    	String ipAddr = CommonUtil.getIpAddr(request);
    	String loginUserStr = stringRedisTemplate.opsForValue().get(ipAddr);
        UserInfo loginUser = JackJsonUtil.jsonToObject(loginUserStr,UserInfo.class);*/
    	// 获取当前的Subject
    	Subject currentUser = SecurityUtils.getSubject();
    	Object principal = currentUser.getPrincipal();
    	UserInfo loginUser = new UserInfo();
        try {
    		 BeanUtils.copyProperties(loginUser, principal);
    	 } catch (IllegalAccessException | InvocationTargetException e) {
    		 e.printStackTrace();
    	 }
        
    	userInfo.setEnable(1);
    	userInfo.setUpdate_user(loginUser.getUserName());
    	userInfo.setUserName(userInfo.getUserName());
    	int updateUserInfoNum = userInfoService.updateUserInfo(userInfo);
    	if(updateUserInfoNum < 1) {
    		throw new RuntimeException("更新用户失败.");
    	}
    	
    	UserRoleInfo userRoleInfo = new UserRoleInfo();
    	userRoleInfo.setUserName(userInfo.getUserName());
    	//获取该用户所有的角色列表
    	List<UserRoleInfo> findUserRoleList = userRoleInfoMapper.findUserRoleByUserName(userRoleInfo);
    	
    	List<UserRoleInfo> bachSave = new ArrayList<>(); //添加用
    	
    	String[] roleIds = userInfo.getRoleIds();
    	if(CollectionUtils.isNotEmpty(findUserRoleList)) {
    		int deleteNum = userRoleInfoMapper.deleteUserRoleInfo(userInfo.getUserName());
    		if(deleteNum < 0) {
    			throw new RuntimeException("更新用户用色关系失败.");
    		}
    	}
    	if(null != roleIds && roleIds.length>0) {
    		for (String role : roleIds) {
    			userRoleInfo = new UserRoleInfo();
    			userRoleInfo.setRoleId(role);
    			userRoleInfo.setUserName(userInfo.getUserName());
    			userRoleInfo.setCreate_user(loginUser.getUserName());
    			userRoleInfo.setUpdate_user(loginUser.getUserName());
    			bachSave.add(userRoleInfo);
			}
    		userRoleNum = userRoleInfoMapper.batchInsertUserRole(bachSave);
    		return userRoleNum;
		}
		return 0;
	}

	@Override
	public List<ExcelUserInfo> exportUserInfo(UserInfo userInfo) throws Exception {
		Map<String,Object> param=new HashMap<>();
		if(!StringUtils.isEmpty(userInfo.getUserName())) {
			param.put("userName",userInfo.getUserName());
		}
		if(!StringUtils.isEmpty(userInfo.getRealName())) {
			param.put("realName",userInfo.getRealName());
		}
		if(null != userInfo.getRoleIds() && userInfo.getRoleIds().length > 0) {
			param.put("roleId",userInfo.getRoleIds()[0]);
		}
		
		List<UserInfo> findUserInfoList = userInfoService.findUserInfoList(param);
		List<ExcelUserInfo> exportUserInfoList =new ArrayList<>();
		ExcelUserInfo excelUserInfo = null;
		if(CollectionUtils.isNotEmpty(findUserInfoList)) {
			for (UserInfo userInfo1 : findUserInfoList) {
				excelUserInfo= new ExcelUserInfo();
				excelUserInfo.setUserName(userInfo1.getUserName());
				excelUserInfo.setRealName(userInfo1.getRealName());
				excelUserInfo.setPassword(userInfo1.getPassword());
				excelUserInfo.setSalt(userInfo1.getSalt());
				excelUserInfo.setEnable(userInfo1.getEnable() == 1 ?"可用":"不可用");
				
				List<RoleInfo> roleList = userInfo1.getRoleList();
				StringBuffer sbRoleId =new StringBuffer();
				StringBuffer sbRoleName =new StringBuffer();
				if(roleList != null) {
					for (RoleInfo roleInfo : roleList) {
						sbRoleId.append(roleInfo.getRoleId()).append(",");
						sbRoleName.append(roleInfo.getRoleName()).append(",");
					}
					;
				}
				excelUserInfo.setRoleId(sbRoleId.toString());
				excelUserInfo.setRoleName((sbRoleName.length() > 0 ? sbRoleName.substring(0,sbRoleName.length()-1):"").toString());
				exportUserInfoList.add(excelUserInfo);
			}
		}
		return exportUserInfoList;
	}

	/**
	 * 导入用户列表
	 */
	@Transactional(rollbackFor= {Exception.class,RuntimeException.class},propagation=Propagation.REQUIRED,isolation=Isolation.DEFAULT)
	@Override
	public String importUserInfo(List<ExcelUserInfo> excelList) throws Exception {
		UserInfo userInfo = null;
		RoleInfo roleInfo = null;
		UserRoleInfo userRoleInfo = null;
		List<UserInfo> userInfoList = new ArrayList<>();
		List<UserRoleInfo> userRoleInfoList = new ArrayList<>();
		for (int i =0;i<excelList.size();i++) {
			ExcelUserInfo excelUserInfo = excelList.get(i);
			userInfo = new UserInfo();
			
			if(excelUserInfo != null) {
				String userName = excelUserInfo.getUserName();
				String realName = excelUserInfo.getRealName();
				String roleId = excelUserInfo.getRoleId();
				if(StringUtils.isEmpty(userName)) {
					throw new RuntimeException("第"+(i+1)+"行的用户名为空.");
				}
				userInfo.setUserName(userName.trim());
				//判断该用户是否存在
				UserInfo existsUserInfo1 = userInfoService.getUserInfo(userInfo);
				UserInfo existsUserInfo = userInfoService.getUserInfoByUserName(userName.trim());
				if( null != existsUserInfo) {
					throw new RuntimeException("第"+(i+1)+"行的用户名已经存在,不能再次添加.") ;
				}
				
				if(StringUtils.isEmpty(realName)) {
					throw new RuntimeException("第"+(i+1)+"行的真实姓名为空.");
				}
				userInfo.setRealName(realName.trim());
				
				String roleIds = excelUserInfo.getRoleId();
				if(StringUtils.isEmpty(roleId) ) {
					throw new RuntimeException("第"+(i+1)+"行的角色为空."); 
				}else {
					String[] roleid = roleIds.trim().split(",");
					for (String strRole : roleid) {
						userRoleInfo = new UserRoleInfo();
						roleInfo = new RoleInfo();
						roleInfo.setRoleId(strRole);
						List<RoleInfo> findRoleList = roleInfoService.findRoleList(roleInfo);
						if(CollectionUtils.isEmpty(findRoleList)) {
							throw new RuntimeException("第"+(i+1)+"行的角色填写错误，不存在这个角色."); 
						}
						userRoleInfo.setUserName(userName.trim());
						userRoleInfo.setRoleId(strRole);
						userRoleInfoList.add(userRoleInfo);
					}
				}
				
				userInfo.setSalt(Constant.SALT);
				userInfo.setPassword(MD5Util.encryptM5(Constant.MD5, "123456", Constant.SALT, Constant.HASHITERATIONS));
				userInfoList.add(userInfo);
			}
		}
		try {
			userInfoService.bathSaveUserInfo(userInfoList);
			userRoleInfoMapper.batchInsertUserRole(userRoleInfoList);
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException("添加用户失败!");
		}
		
		return Constant.SUCC;
	}
    
}
