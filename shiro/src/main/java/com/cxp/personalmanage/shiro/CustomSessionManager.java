package com.cxp.personalmanage.shiro;

import java.io.Serializable;

import javax.servlet.ServletRequest;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义SessionManager
 * @author Administrator
 *
 */
public class CustomSessionManager extends DefaultWebSessionManager {
	
	private Logger logger = LoggerFactory.getLogger(CustomSessionManager.class);
	
	/**
	 * 优化单次请求需要多次访问redis的问题
	 */
	@Override
	protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
		Serializable sessionId = getSessionId(sessionKey);

		ServletRequest request = null;
		if (sessionKey instanceof WebSessionKey) {
			request = ((WebSessionKey) sessionKey).getServletRequest();
		}
		if (request != null && sessionId != null) {
			Session session = (Session) request.getAttribute(sessionId.toString());
			if (session != null) {
				return session;
			}
		}
		Session session = super.retrieveSession(sessionKey);
		if (request != null && sessionId != null) {
			request.setAttribute(sessionId.toString(), session);
		}
		return session;
	}
}
