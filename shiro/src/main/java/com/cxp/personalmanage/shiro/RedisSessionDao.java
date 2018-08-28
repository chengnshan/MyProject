package com.cxp.personalmanage.shiro;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.crazycake.shiro.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import com.cxp.personalmanage.utils.JedisUtil;

public class RedisSessionDao extends AbstractSessionDAO {

	private static final Logger logger = LoggerFactory.getLogger(RedisSessionDao.class);

	private final String shiro_session_prefix = "shiro_";
	private static final int DEFAULT_EXPIRE = 1800;
	private int expire = DEFAULT_EXPIRE;

	@Autowired
	private JedisUtil jedisUtil;

	private byte[] getKey(String key) {
		return (shiro_session_prefix + key).getBytes();
	}

	private void saveSession(Session session) {
		if (session == null || session.getId() == null) {
			logger.error("session or session id is null");
			throw new UnknownSessionException("session or session id is null");
		}
		try {
			byte[] key = getKey(session.getId().toString());
			byte[] value = SerializationUtils.serialize(session);
			jedisUtil.set(key, value);
			jedisUtil.expire(key, DEFAULT_EXPIRE);
		} catch (Exception e) {
			logger.error("serialize session error. session id=" + session.getId());
			throw new UnknownSessionException(e);
		}
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		this.saveSession(session);
	}

	@Override
	public void delete(Session session) {
		if (session == null || session.getId() == null) {
			logger.error("session or session id is null");
			return;
		}
		try {
			byte[] key = getKey(session.getId().toString());
			jedisUtil.del(key);
		} catch (Exception e) {
			logger.error("delete session error. session id=" + session.getId());
		}
	}

	/**
	 * 获取存活的session
	 */
	@Override
	public Collection<Session> getActiveSessions() {
		Set<byte[]> keys = jedisUtil.keys(shiro_session_prefix);
		Set<Session> sessions = new HashSet<>();
		if (CollectionUtils.isEmpty(keys)) {
			return sessions;
		}
		for (byte[] key : keys) {
			Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
			sessions.add(session);
		}
		return sessions;
	}

	@Override
	protected Serializable doCreate(Session session) {
		if (session == null) {
			logger.error("session is null");
			throw new UnknownSessionException("session is null");
		}
		Serializable sessionId = generateSessionId(session);
		this.assignSessionId(session, sessionId);
		this.saveSession(session);
		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		System.out.println("doReadSession session");
		if (sessionId == null) {
			return null;
		}
		byte[] key = getKey(sessionId.toString());
		byte[] value = jedisUtil.get(key);
		return (Session) SerializationUtils.deserialize(value);
	}

	@Override
	public Session readSession(Serializable sessionId) throws UnknownSessionException {
		Session s = doReadSession(sessionId);
		if (s == null) {
			throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
		}
		return s;
	}
}
