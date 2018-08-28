package com.cxp.personalmanage.config.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.RandomSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.cxp.personalmanage.shiro.CustomSessionManager;
import com.cxp.personalmanage.shiro.RedisCache;
import com.cxp.personalmanage.shiro.RedisCacheManager;
import com.cxp.personalmanage.shiro.RedisSessionDao;
import com.cxp.personalmanage.shiro.UserRealm;
import com.cxp.personalmanage.shiro.filter.KickoutSessionControlFilter;

//@Configuration
public class ShrioConfig {
	private static final Logger logger = LoggerFactory.getLogger(ShrioConfig.class);

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
		logger.info("注入Shiro的web过滤器：ShiroFilter", ShiroFilterFactoryBean.class);
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// Shiro的核心安全接口，这个属性必须的
		shiroFilterFactoryBean.setSecurityManager(securityManager);

		//自定义拦截器
		Map<String, Filter> filtersMap = new LinkedHashMap<>(); // 过滤器
		//限制同一帐号同时在线的个数。
		filtersMap.put("kickout", kickoutFilter());
		shiroFilterFactoryBean.setFilters(filtersMap);
		
		// 用户访问未对其授权的资源时,所显示的连接
		shiroFilterFactoryBean.setUnauthorizedUrl("/html/403.html");
		// 要求登录时的链接(可根据项目的URL进行替换),非必须的属性,如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/html/login.html");
		shiroFilterFactoryBean.setSuccessUrl("/html/index.html");

		// 拦截器.
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		// 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
		filterChainDefinitionMap.put("/logout", "logout");
		// <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
		// <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/html/login.html", "anon");
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/defaultKaptcha", "anon");
		filterChainDefinitionMap.put("/html/kickout.html", "anon");
		filterChainDefinitionMap.put("/**", "kickout,authc");
//		filterChainDefinitionMap.put("/**", "authc");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return shiroFilterFactoryBean;
	}

	/**
	 * 不指定名字的话，自动创建一个方法名第一个字母小写的bean
	 *
	 * @return
	 * @Bean(name = "securityManager")
	 */
	@Bean
	public SecurityManager securityManager() {
		logger.info("注入Shiro的安全管理器-->securityManager", SecurityManager.class);
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm,用于获取认证
		securityManager.setRealm(userRealm());
		// 自定义session管理 使用redis
		securityManager.setSessionManager(sessionManager());
		// 自定义缓存实现 使用redis
		securityManager.setCacheManager(cacheManager());// 这个如果执行多次，也是同样的一个对象;
		return securityManager;
	}

	/**
	 * Shiro Realm 继承自AuthorizingRealm的自定义Realm,即指定Shiro验证用户登录的类为自定义的
	 *
	 * @param //cacheManager
	 * @return
	 */
	@Bean
	public UserRealm userRealm() {
		UserRealm userRealm = new UserRealm();
		// 告诉realm,使用credentialsMatcher加密算法类来验证密文
		userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
		userRealm.setCachingEnabled(true);
		// 启用缓存,默认是false userRealm.setCachingEnabled(true); //
		// 启用身份验证缓存，即缓存AuthenticationInfo信息，默认false；
		userRealm.setAuthenticationCachingEnabled(true); //
		// 缓存AuthenticationInfo信息的缓存名称,即配置在ehcache.xml中的cache name
		userRealm.setAuthenticationCacheName("authenticationCache"); //
		// 启用授权缓存，即缓存AuthorizationInfo信息，默认false；
		userRealm.setAuthorizationCachingEnabled(true); //
		// 缓存AuthorizationInfo信息的缓存名称；
		userRealm.setAuthorizationCacheName("authorizationCache");

		return userRealm;
	}

	/**
	 * 凭证匹配器 （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
	 * 所以我们需要修改下doGetAuthenticationInfo中的代码; ） 可以扩展凭证匹配器，实现 输入密码错误次数后锁定等功能，下一次
	 *
	 * @return
	 */
	@Bean(name = "credentialsMatcher")
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		hashedCredentialsMatcher.setHashAlgorithmName("md5");// 散列算法:这里使用MD5算法;
		hashedCredentialsMatcher.setHashIterations(3);// 散列的次数，比如散列两次，相当于 md5(md5(""));
		// storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
		hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
		return hashedCredentialsMatcher;
	}

	/**
	 * Shiro生命周期处理器
	 *
	 * @return
	 */
	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	public SessionManager sessionManager() {
//		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		// <!-- 删除失效的session -->
		CustomSessionManager sessionManager = new CustomSessionManager();
		// <!-- 会话超时时间，单位：毫秒 -->
		sessionManager.setGlobalSessionTimeout(600000);
		sessionManager.setDeleteInvalidSessions(true);
		//是否定期检查Session的有效性 
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.setCacheManager(cacheManager());
		sessionManager.setSessionDAO(sessionDAO());
		return sessionManager;
	}
	
	@Bean
	public SessionDAO sessionDAO() {
		RedisSessionDao redisSessionDAO = new RedisSessionDao();
		//设置生成sessionId生成策略:
		//RandomSessionIdGenerator() 和 JavaUuidSessionIdGenerator()
		redisSessionDAO.setSessionIdGenerator(new RandomSessionIdGenerator());
		return redisSessionDAO;
	}
	
	/**
	 * shiro缓存管理器; 需要注入对应的其它的实体类中： 1、安全管理器：securityManager
	 * 可见securityManager是整个shiro的核心；
	 *
	 * @return
	 */
	@Bean
	public CacheManager cacheManager() {
		logger.info("注入Shrio的缓存管理器-->ehCacheManager" + EhCacheManager.class);
		RedisCacheManager cacheManager = new RedisCacheManager();
//		cacheManager.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
		return cacheManager;
	}
	
	/**
	 *  kickoutSessionControlFilter 用于控制并发登录人数的 
	 * @return
	 */
	@Bean
	public AccessControlFilter kickoutFilter() {
		KickoutSessionControlFilter kickout=new KickoutSessionControlFilter();
		//是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
		kickout.setKickoutAfter(false);
		//被踢出后重定向到的地址；
		kickout.setKickoutUrl("/html/kickout.html");
		//同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
		kickout.setMaxSession(1);
		//用于根据会话ID，获取会话进行踢出操作的；
		kickout.setSessionManager(sessionManager());
		//使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
    	//这里我们还是用之前shiro使用的redisManager()实现的cacheManager()缓存管理
    	//也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
		kickout.setCacheManager(cacheManager());
		
		return kickout;
	}
}
