package com.cxp.personalmanage.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cxp.personalmanage.common.Constant;
import com.cxp.personalmanage.controller.base.BaseController;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.crazycake.shiro.IRedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.utils.JackJsonUtil;

@RequestMapping(value = "/session")
@Controller
public class SessionController extends BaseController{

	private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

	@Autowired
	@Qualifier(value = "redisSessionDAO")
	private RedisSessionDAO redisSessionDAO;

	@RequestMapping(value = "/findOnlineUserList")
	@ResponseBody
	@RequiresRoles(value = {"admin","user"},logical = Logical.AND)
	public String findOnlineUserList(String userName,@RequestParam(defaultValue = "1",required = false) String currentPage) {
		logger.info("findOnlineUserList 入参 : " + userName);

		Map<String,Object> map=new HashMap<>();
		Collection<Session> activeSessions = redisSessionDAO.getActiveSessions();

		//获取已经登录的用户
		if(CollectionUtils.isNotEmpty(activeSessions)) {
			map.put("sessionSize", activeSessions.size());
		}
		
		Map<String, Object> onlineMap = null;
		List<Map<String, Object>> onlineList = new ArrayList<>();
		String sessionStr = null;
		for (Session session : activeSessions) {
			onlineMap = new HashMap<>();
			onlineMap.put("sessionId", session.getId());
			onlineMap.put("host", session.getHost());
			onlineMap.put("startTimestamp", session.getStartTimestamp());
			onlineMap.put("lastAccessTime", session.getLastAccessTime());
			String key = Constant.REDISDAO_LOGIN_PREFIX + session.getId();
			IRedisManager redisManager = redisSessionDAO.getRedisManager();
			try {
				SimpleSession deserialize = (SimpleSession) redisSessionDAO.getValueSerializer()
						.deserialize(redisManager.get(key.getBytes()));
				SimplePrincipalCollection attribute = (SimplePrincipalCollection) deserialize
						.getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
				if(attribute != null) {
					UserInfo userInfo = new UserInfo();
					try {
						BeanUtils.copyProperties(userInfo, attribute.getPrimaryPrincipal());
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
					logger.info(userInfo.toString());
					onlineMap.put("userInfo", userInfo);
					onlineList.add(onlineMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//根据onlineList分页数据
		int size = onlineList.size();
		List<Map<String, Object>> onlineListPage = new ArrayList<>();
		if(size > 10){
			for(int i = (Integer.parseInt(currentPage)-1)* Constant.ROWS; i< Integer.parseInt(currentPage)*10 ; i++){
				onlineListPage.add(onlineList.get(i));
			}
		}else{
			onlineListPage.addAll(onlineList);
		}
		map.put("onlineList", onlineListPage);
//		sessionStr = JackJsonUtil.objectToString(map);

		return buildSuccessResultInfo(onlineList != null ?onlineList.size() : 0,map);
	}

	@RequiresRoles(value = {"admin","user"},logical = Logical.OR)
	@RequestMapping(value = "/kickoutSession")
	@ResponseBody
	public String kickoutSession(String sessionId, RedirectAttributes redirectAttributes) {
		/*try {
			Session session = redisSessionDAO.readSession(sessionId);
			if (session != null) {
				session.setAttribute("kickout", Boolean.TRUE);
				redisSessionDAO.delete(session);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		org.apache.shiro.mgt.SecurityManager securityManager = SecurityUtils.getSecurityManager();
		Subject.Builder builder = new Subject.Builder(securityManager);
		builder.sessionId(sessionId);
		Subject subject = builder.buildSubject();
		if (null != subject) {
			try {
				subject.logout();
			} catch (SessionException e) {
				// TODO: handle exception
				logger.error("OAClientService;userLogout;", e);
			}
		}
//		redirectAttributes.addFlashAttribute("msg", "强制退出成功！");
//		return "redirect:/session/findOnlineUserList";
		return "{\"status\":\"200\",\"msg\":\"强制退出成功！\"}";
	}
	
	@RequestMapping(value = "/getLocalSessionId")
	@ResponseBody
	public String getLocalSessionId() {
		Map<String,Object> map =new HashMap<>();
		
		Subject subject = SecurityUtils.getSubject();
		Session localSession = subject.getSession();
		map.put("sessionId", localSession.getId());
		try {
			return JackJsonUtil.obj2json(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
