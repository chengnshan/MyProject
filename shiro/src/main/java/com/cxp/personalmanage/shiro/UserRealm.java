package com.cxp.personalmanage.shiro;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.cxp.personalmanage.service.UserInfo_Service;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cxp.personalmanage.pojo.RoleInfo;
import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.service.RoleInfoService;
import com.cxp.personalmanage.service.UserInfoService;

public class UserRealm extends AuthorizingRealm {

	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private RoleInfoService roleInfoService;

	@Override
	public String getName() {
		return "userRealm";
	}

	/**
	 * 权限
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Object principal = principals.getPrimaryPrincipal();
		UserInfo userInfo = new UserInfo();
		try {
			BeanUtils.copyProperties(userInfo, principal);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		List<RoleInfo> roleList = roleInfoService.findUserRoleInfoList(userInfo.getUserName());

		List<String> userRoles = new ArrayList<>();
		if (!CollectionUtils.isEmpty(roleList)) {
			for (RoleInfo list : roleList) {
				userRoles.add(list.getRoleId());
			}
		}
		// 为当前角色设置角色和权限
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		authorizationInfo.addRoles(userRoles);
		return authorizationInfo;
	}

	/**
	 * 密码认证,验证当前登录的subject
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 获取表单的用户名
		String userName = (String) token.getPrincipal();
		UserInfo userInfo = new UserInfo();
		userInfo.setUserName(userName);
		try {
			// 从数据库查询用户
			userInfo = userInfoService.getUserInfo(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (userInfo == null) {
			// 没有找到账号
			throw new UnknownAccountException();
		}
		// 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userInfo, userInfo.getPassword(),
				new MySimpleByteSource(userInfo.getSalt()), getName());
		return authenticationInfo;
	}

	@Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
}
