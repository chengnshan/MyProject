package com.cxp.personalmanage.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cxp.personalmanage.utils.CommonUtil;
import com.cxp.personalmanage.utils.JackJsonUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cxp.personalmanage.pojo.UserInfo;
import com.cxp.personalmanage.utils.VerifyCodeUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Resource(name = "captchaProducer")
	private DefaultKaptcha defaultKaptcha;

	@Autowired
	@Qualifier(value = "stringRedisTemplate")
	private StringRedisTemplate stringRedisTemplate;

	@RequestMapping(value = "/login")
	@ResponseBody
	public String login(UserInfo userInfo, HttpServletRequest request, Map<String, Object> map,String randomcode) {
		String username = userInfo.getUserName();
		// 页面输入的验证码
		String sessionRandomcode = (String) request.getSession().getAttribute("vrifyCode");
		if(sessionRandomcode == null) {
			return "{\"msg\":\"验证码已失效,请重新获取!\",\"status\":\"100\"}";
		}
		if (randomcode != null && !randomcode.equalsIgnoreCase(sessionRandomcode)) {
			return "{\"msg\":\"验证码错误!\",\"status\":\"100\"}";
		}
		
		System.out.println(username);
		// 获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(userInfo.getUserName(), userInfo.getPassword());
		String msg = "";
		try {
			// 在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
			// 每个Realm都能在必要时对提交的AuthenticationTokens作出反应
			// 所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
			logger.info("对用户[" + username + "]进行登录验证..验证开始");
			currentUser.login(token);

//			String ipAddr = CommonUtil.getIpAddr(request);
//			stringRedisTemplate.opsForValue().set(ipAddr, JackJsonUtil.objectToString(currentUser.getPrincipal()),1800,TimeUnit.SECONDS);
			logger.info("对用户[" + username + "]进行登录验证..验证通过");
		} catch (UnknownAccountException uae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
			msg = "验证未通过,未知账户";
		} catch (IncorrectCredentialsException ice) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
			msg = "验证未通过,错误的凭证";
		} catch (LockedAccountException lae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
			msg = "验证未通过,账户已锁定";
		} catch (ExcessiveAttemptsException eae) {
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数过多");
			msg = "验证未通过,错误次数过多,请5分钟后再尝试登录";
		} catch (AuthenticationException ae) {
			// 通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
			ae.printStackTrace();
		}
		// 验证是否登录成功
		if (currentUser.isAuthenticated()) {
			logger.info("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
			return "{\"msg\":\"../html/index.html\",\"status\":\"200\"}";
		} else {
			token.clear();
			return "{\"msg\":\""+msg+"\",\"status\":\"100\"}";
		}
	}

	@RequestMapping("/defaultKaptcha")
	public void defaultKaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*byte[] captchaChallengeAsJpeg = null;
		HttpSession session = null;
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
		try {
			// 生产验证码字符串并保存到session中
			String createText = defaultKaptcha.createText();
			logger.info("验证码为：" + createText);
			session = request.getSession();
			session.setAttribute("vrifyCode", createText);
			// 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
			BufferedImage challenge = defaultKaptcha.createImage(createText);
			ImageIO.write(challenge, "jpg", jpegOutputStream);
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
		captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		ServletOutputStream responseOutputStream = response.getOutputStream();
		responseOutputStream.write(captchaChallengeAsJpeg);
		responseOutputStream.flush();
		responseOutputStream.close();
		this.removeVrityCode(session, "vrifyCode");*/
		
		try {
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/jpeg");
			// 生成随机字串
			String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
			logger.info("验证码为：" + verifyCode);
			// 存入会话session
			HttpSession session = request.getSession(true);
			String attrName = "vrifyCode";
			session.setAttribute(attrName, verifyCode.toLowerCase());
			// 生成验证码流
			int w = 99, h = 38;
			VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
		} catch (Exception e) {
			logger.error("获取验证码异常：{}",e.getCause().toString());
		}
	}
	
	/**
	 * 设置定时删除session中的验证码
	 * @param session
	 * @param attrName
	 */
	private void removeVrityCode(final HttpSession session, final String attrName) {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// 删除session中存的验证码
				if(null != session.getAttribute(attrName)) {
					session.removeAttribute(attrName);
					timer.cancel();
				}
			}
		}, 2 * 60 * 1000);
	}
}
