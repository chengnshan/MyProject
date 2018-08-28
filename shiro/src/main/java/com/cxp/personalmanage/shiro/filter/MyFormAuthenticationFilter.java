package com.cxp.personalmanage.shiro.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import com.cxp.personalmanage.pojo.UserInfo;

/**
 * shiro使用FormAuthenticationFilter进行表单认证，验证校验的功能应该加在FormAuthenticationFilter中，在认证之前进行验证码校验。
 * 
 * @author Administrator
 *
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {
	/**
	 * 每次登录都会到这里来，这里用来处理 不注销之前已登录用户下，再次登录
	 * isAccessAllowed：表示是否允许访问；mappedValue就是[urls]配置中拦截器参数部分，如果允许访问返回true，否则false；
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (isLoginRequest(request, response)) {
			if (isLoginSubmission(request, response)) {
				// 本次用户登陆账号
				String account = this.getUsername(request);
				Subject subject = this.getSubject(request, response);
				// 之前登陆的用户
				UserInfo userInfo = (UserInfo) subject.getPrincipal();
				// 如果两次登陆的用户不一样，则先退出之前登陆的用户,（有问题，相同用户无法跳转页面）
				// 解决：可以不判断，都退出之前的登录，再重新登录
		/*	if (account != null && userInfo != null) {
					// 获取session，获取验证码
					HttpServletRequest httpServletRequest = (HttpServletRequest) request;
					HttpSession session = httpServletRequest.getSession();
					String sRand = (String) session.getAttribute("vrifyCode");
					// 注销登录，同时会使session失效
					subject.logout();
					// 所以重新设置session
					HttpSession session1 = httpServletRequest.getSession();
					session1.setAttribute("vrifyCode", sRand);
				}*/
				return false;
			}
		}
		return super.isAccessAllowed(request, response, mappedValue);
	}

	/**
	 * 验证码验证
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		// 在这里进行验证码的校验
		// 从session获取正确的验证码
		HttpSession session = ((HttpServletRequest) request).getSession();
		// 页面输入的验证码
		String randomcode = request.getParameter("randomcode");
		// 从session中取出验证码
		String validateCode = (String) session.getAttribute("vrifyCode");
		if (randomcode != null && validateCode != null) {
			if (!randomcode.equalsIgnoreCase(validateCode)) {
				// randomCodeError表示验证码错误
				request.setAttribute("shiroLoginFailure", "kaptchaValidateFailed");
				// 拒绝访问，不再校验账号和密码
				return true;
			}
		}
		return super.onAccessDenied(request, response, mappedValue);
	}

	/**
	 * 重写FormAuthenticationFilter的onLoginSuccess方法 指定的url传递进去，这样就实现了跳转到指定的页面；
	 */
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		WebUtils.getAndClearSavedRequest(request);// 清理了session中保存的请求信息
		WebUtils.redirectToSavedRequest(request, response, getSuccessUrl());
		return false;
	}

}
