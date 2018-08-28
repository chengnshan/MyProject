package com.cxp.personalmanage.utils;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.cxp.personalmanage.pojo.UserInfo;

public class CommonUtil {
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static UserInfo getCurrentLoginUser() {
		// 获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		Object principal = currentUser.getPrincipal();
		UserInfo userInfo = null;
		try {
			if (null != principal) {
				userInfo = new UserInfo();
				BeanUtils.copyProperties(userInfo, principal);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return userInfo;
	}
}
