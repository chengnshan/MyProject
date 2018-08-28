package com.cxp.personalmanage.controller;

import com.cxp.personalmanage.mapper.StudentMapper;
import com.cxp.personalmanage.pojo.Student;
import com.cxp.personalmanage.pojo.UserInfo;

import com.cxp.personalmanage.service.UserInfo_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {
	@Autowired
	private StudentMapper studentMapper;

	@Autowired
	private UserInfo_Service userInfoService;

	@RequestMapping(value = "/findStudentList")
	public List<Student> findStudentList(String name, String student_id) {
		System.out.println(studentMapper.findAll());
		Map<String, Object> param = new HashMap<>();
		param.put("name", name);
		param.put("student_id", student_id);
		return studentMapper.findStudentByCondition(param);
	}

	@RequestMapping(value = "/findUserInfoList")
	public List<UserInfo> findUserInfoList(String userName, String roleId) {
		Map<String, Object> param = new HashMap<>();
		param.put("userName", userName);
		param.put("roleId", roleId);
		List<UserInfo> userInfoList = null;//userInfoService.findUserInfoList(param);
		UserInfo userInfo = new UserInfo();
		userInfo.setUserName("013456");
//		System.out.println(userInfoService.getUserInfo(userInfo));
		return userInfoList;
	}

	@RequestMapping(value = "/saveUserInfo", method = RequestMethod.POST)
	public int saveUserInfo(UserInfo userInfo) {
		int result = 0;
		if (null != userInfo) {
			userInfo.setCreate_time(new Date());
			userInfo.setUpdate_time(new Date());
//			result = userInfoService.saveUserInfo(userInfo);
		}

		return result;
	}
}
