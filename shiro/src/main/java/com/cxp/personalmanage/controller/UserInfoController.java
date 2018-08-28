package com.cxp.personalmanage.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cxp.personalmanage.common.Constant;
import com.cxp.personalmanage.controller.base.BaseController;
import com.cxp.personalmanage.pojo.MenuInfo;
import com.cxp.personalmanage.pojo.RoleInfo;
import com.cxp.personalmanage.service.MenuInfoService;
import com.cxp.personalmanage.service.RoleInfoService;
import com.cxp.personalmanage.service.UserInfoService;
import com.cxp.personalmanage.service.UserInfo_Service;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.pojo.excel.ExcelUserInfo;
import com.cxp.personalmanage.utils.ExcelUtil;
import com.cxp.personalmanage.utils.JackJsonUtil;

@RestController
@RequestMapping(value="/admin")
public class UserInfoController extends BaseController{
	private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

	@Autowired
	@Qualifier(value = "stringRedisTemplate")
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private MenuInfoService menuInfoService;
	@Autowired
	private RoleInfoService roleInfoService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private UserInfo_Service userInfo_Service;

	@RequestMapping(value = "/blog.html")
	public String blogPage() {
		return "html/template/blog";
	}

	/**
	 * 获取登录用户、角色所拥有的菜单
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getLoginUser")
	public String index(HttpServletRequest request) throws Exception {
		// 获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		Object principal = currentUser.getPrincipal();
		UserInfo userInfo = new UserInfo();
		try {
			BeanUtils.copyProperties(userInfo, principal);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		String userMenuKey = Constant.LOGIN_MENU_INFO + userInfo.getUserName();
		String userMenuStr = stringRedisTemplate.opsForValue().get(userMenuKey);
		if(!StringUtils.isEmpty(userMenuStr)){
			logger.info("菜单查询的是redis.");
			return userMenuStr;
		}
		List<RoleInfo> userRoleInfoList = roleInfoService.findUserRoleInfoList(userInfo.getUserName());
		Set<String> set=new HashSet<>();
		if(CollectionUtils.isNotEmpty(userRoleInfoList)){
			for(RoleInfo roleInfo : userRoleInfoList){
				set.add(roleInfo.getRoleId());
			}
		}
		Map<String,Object> paramMap=new HashMap<>();
		paramMap.put("roleIds",set);
		List<MenuInfo> menuListByRoleId = menuInfoService.findMenuListByRoleId(paramMap);
		paramMap.put("userInfo",userInfo);
		paramMap.put("menuListByRoleId",menuListByRoleId);
		logger.info("菜单查询的是数据库.");
		String userMenu = JackJsonUtil.obj2json(paramMap);
		stringRedisTemplate.opsForValue().set(userMenuKey,userMenu,1800, TimeUnit.SECONDS);
		return userMenu;
	}

	/**
	 * 查询用户列表
	 * @param roleId
	 * @param userName
	 * @param realName
	 * @param currentPage
	 * @return
	 */
	@RequestMapping(value = "/queryUserListByCondition" )
	public String queryUserListByCondition(String roleId, String userName, String realName,
										   @RequestParam(defaultValue = "1",required = false) String currentPage){
		Map<String,Object> param=new HashMap<>();
		param.put("pageSize", Constant.ROWS);
		if(currentPage != null && !"".equals(currentPage)){
			param.put("pageRow",(Integer.parseInt(currentPage)-1)*10);
		}

		if ( !StringUtils.isEmpty(roleId)){
			param.put("roleId",roleId);
		}
		if ( !StringUtils.isEmpty(userName)){
			param.put("userName",userName);
		}
		if ( !StringUtils.isEmpty(realName)){
			param.put("realName",realName);
		}

		List<UserInfo> userInfoList = userInfoService.findUserInfoList(param);
		Integer userInfoListCount = userInfoService.findUserInfoListCount(param);

		return buildSuccessResultInfo(userInfoListCount,userInfoList);
	}

	/**
	 * 添加用户
	 * @param addUserName
	 * @param addRealName
	 * @param addRoleId
	 * @param addUserPassword
	 * @return
	 */
	@RequestMapping(value = "/addUserInfoAndRole")
	public String addUserInfoAndRole(String addUserName,String addRealName,String addRoleId,String addUserPassword){
			logger.info("addUserInfoAndRole 入参: addUserName :"+addUserName+",addRealName :"+addRealName
					+",addRoleId :"+addRoleId
					+",addUserPassword :"+addUserPassword);
		if( !StringUtils.isEmpty(addUserName)){
			addUserName = addUserName.trim();
		}
		if( !StringUtils.isEmpty(addRealName)){
			addRealName= addRealName.trim();
		}
		if( !StringUtils.isEmpty(addUserPassword)){
			addUserPassword = addUserPassword.trim();
		}
		if( !StringUtils.isEmpty(addRoleId)){
			addRoleId = addRoleId.trim();
		}

		try {
			UserInfo userInfo =new UserInfo();
			userInfo.setUserName(addUserName);
			UserInfo existsUser = userInfoService.getUserInfo(userInfo);
			if(null != existsUser) {
				return buildFailedResultInfo(-1, "该用户已经存在,请确认后再添加!");
			}
			userInfo_Service.saveUserInfoAndRole( addUserName, addRealName, addRoleId, addUserPassword);
		} catch (Exception e) {
			logger.info("添加用户出错误.",e.getMessage());
			return buildFailedResultInfo(-1,"添加用户失败.");
		}
		return buildSuccessResultInfo(1,"添加用户成功.");
	}

	/**
	 * 删除用户和角色
	 * @param userName
	 * @return
	 */
	@RequestMapping(value = "/deleteUserInfoAndRole")
	public String deleteUserInfoAndRole(String userName){

		if( !StringUtils.isEmpty(userName)){
			try {
				userInfo_Service.deleteUserInfoAndRole(userName);
			} catch (Exception e) {
				logger.info("删除用户出失败 .",e.getMessage());
				return buildFailedResultInfo(-1,"删除用户失败!");
			}
		}
		return buildSuccessResultInfo(1,"删除用户成功");
	}
	
	/**
	 * 根据用户名获取用户对应的信息
	 * @param userName
	 * @return
	 */
	@RequestMapping("/getUserInfoByUserName")
	public String getUserInfoByUserName(String userName) {
		if( !StringUtils.isEmpty(userName)){
			Map<String, Object> paramMap =new HashMap<String, Object>();
			UserInfo userInfo = new UserInfo();
			userInfo.setUserName(userName);
			UserInfo userInfo2 = userInfoService.getUserInfo(userInfo);
			List<RoleInfo> findRoleList = roleInfoService.findRoleList(null);
			paramMap.put("userInfo", userInfo2);
			paramMap.put("roleList", findRoleList);
			return buildSuccessResultInfo(1, paramMap);
		}
		return buildFailedResultInfo(-1, "查询不到这个用户.");
	}
	
	/**
	 * 更新用户信息和角色
	 * @param userInfo
	 * @return
	 */
	@RequestMapping(value="/updateUserInfoAndRole")
	public String updateUserInfoAndRole(@RequestBody UserInfo userInfo) {
		logger.info("addUserInfoAndRole 入参: userInfo : "+userInfo);
		if(null != userInfo && userInfo.getUserName() != null) {
			try {
				userInfo_Service.updateUserAndRole(userInfo);
			} catch (Exception e) {
				e.printStackTrace();
				return buildFailedResultInfo(-1, "更新用户失败!");
			}
		}
		return buildSuccessResultInfo(1,"更新用户成功!");
	}
	
	/**
	 * 下载用户列表
	 * @param userInfo
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/exportUserInfo")
	public String exportUserInfo(UserInfo userInfo,HttpServletResponse response) {
		OutputStream out = null;
        try {
			
			ExcelUtil<ExcelUserInfo> excelUtil = new ExcelUtil<>(ExcelUserInfo.class);
			List<ExcelUserInfo> exportUserInfoList = userInfo_Service.exportUserInfo(userInfo);
			response.setHeader("content-type", "application/octet-stream");
			response.setHeader("Content-Disposition",
			        "attachment;filename=" + new String("用户列表.xls".getBytes("utf-8"), "iso8859-1"));
			response.setContentType("application/octet-stream;charset=UTF-8");
			out = response.getOutputStream();
			excelUtil.exportExcel(exportUserInfoList, "用户列表", out);
		} catch (UnsupportedEncodingException e) {
			logger.error("下载用户列表出错.",e.getMessage());
			return buildFailedResultInfo(-1, "服务器出现问题,请稍后重试!");
		}catch(IOException e) {
			logger.error("下载用户列表出错.",e.getMessage());
			return buildFailedResultInfo(-1, "服务器出现问题,请稍后重试!");
		}catch(Exception e) {
			logger.error("下载用户列表出错.",e.getMessage());
			return buildFailedResultInfo(-1, "服务器出现问题,请稍后重试!");
		}finally {
			if(out != null) {
				try {
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return buildSuccessResultInfo("下载用户成功!");
	}
	
	@RequestMapping(value="/importUserInfo",method=RequestMethod.POST)
	public String importUserInfo(HttpServletResponse response,@RequestParam("file-Portrait") MultipartFile file,
            HttpServletRequest request,Integer id,String userName) {
		logger.info("入参：id = "+id + ",userName = "+userName);
		InputStream in = null;
		if(!file.isEmpty()) {
			String saveFileName = file.getOriginalFilename();
			logger.info("上传文件解析开始.文件名为:"+saveFileName);
			try {
				in = file.getInputStream();
				ExcelUtil<ExcelUserInfo> importExcel = new ExcelUtil<ExcelUserInfo>(ExcelUserInfo.class);
				List<ExcelUserInfo> excelList = importExcel.importExcel("用户列表", in);
				System.out.println(excelList);
				for (ExcelUserInfo excelUserInfo : excelList) {
					System.out.println(excelUserInfo);
				}
				String checkResult = "";
				if(CollectionUtils.isNotEmpty(excelList)) {
					checkResult = userInfo_Service.importUserInfo(excelList);
				}
				if(Constant.SUCC.equals(checkResult)) {
					return buildSuccessResultInfo("上传文件成功.");
				}
				
			} catch (Exception e) {
				logger.error("上传文件出错.",e.getMessage());
				return buildFailedResultInfo(-1, e.getMessage());
			}finally {
				try {
					if(in != null) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			return buildFailedResultInfo(-1, "上传文件为空!");
		}
		return buildFailedResultInfo(-1, "上传文件失败!");
	}
}
