package com.cxp.personalmanage.shiro.listener;

import java.util.Date;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

public class MyShiroSessionListener implements SessionListener {

	@Override
	public void onStart(Session session) {
		System.out.println("会话创建：" + session.getId() + "》》时间：" + session.getLastAccessTime());
	//	session.setTimeout(600000);
	}

	@Override
	public void onExpiration(Session session) {// 会话过期时触发
		System.out.println("会话过期：" + session.getId() + "》》时间：" + new Date());
	}

	@Override
	public void onStop(Session session) {// 退出/会话过期时触发
		System.out.println("会话停止：" + session.getId() + "》》时间：" + session.getLastAccessTime());
	}
}
