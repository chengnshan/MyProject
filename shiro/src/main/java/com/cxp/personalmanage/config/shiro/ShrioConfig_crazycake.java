package com.cxp.personalmanage.config.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.RandomSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.ObjectSerializer;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.crazycake.shiro.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cxp.personalmanage.common.Constant;
import com.cxp.personalmanage.shiro.CustomSessionManager;
import com.cxp.personalmanage.shiro.RetryLimitHashedCredentialsMatcher;
import com.cxp.personalmanage.shiro.UserRealm;
import com.cxp.personalmanage.shiro.filter.CustomLogout;
import com.cxp.personalmanage.shiro.filter.KickoutSessionControlFilter;
import com.cxp.personalmanage.shiro.listener.MyShiroSessionListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

@Configuration
public class ShrioConfig_crazycake implements EnvironmentAware{
	private static final Logger logger = LoggerFactory.getLogger(ShrioConfig_crazycake.class);
	
	private Environment enviroment;
	
	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
		logger.info("注入Shiro的web过滤器：ShiroFilter", ShiroFilterFactoryBean.class);
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// Shiro的核心安全接口，这个属性必须的
		shiroFilterFactoryBean.setSecurityManager(securityManager);

		//自定义拦截器
		Map<String, Filter> filtersMap = new LinkedHashMap<>(); // 过滤器
 		CustomLogout customLogout = new CustomLogout();
		customLogout.setRedirectUrl("/html/login1.html");
		//限制同一帐号同时在线的个数。
		filtersMap.put("kickout", kickoutFilter());
 		filtersMap.put("customLogout", customLogout);
		shiroFilterFactoryBean.setFilters(filtersMap);

		// 用户访问未对其授权的资源时,所显示的连接
		shiroFilterFactoryBean.setUnauthorizedUrl("/html/403.html");
		// 要求登录时的链接(可根据项目的URL进行替换),非必须的属性,如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/html/login1.html");
		shiroFilterFactoryBean.setSuccessUrl("/html/index.html");

		// 拦截器.
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		// 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
		filterChainDefinitionMap.put("/logout", "customLogout");
		// <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
		// <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/html/login1.html", "anon");
		filterChainDefinitionMap.put("/html/login.html", "anon");
		filterChainDefinitionMap.put("/login", "anon");
		filterChainDefinitionMap.put("/defaultKaptcha", "anon");
		filterChainDefinitionMap.put("/html/kickout.html", "anon");
		filterChainDefinitionMap.put("/webchat/**", "anon");
		filterChainDefinitionMap.put("/image/weixin/**", "anon");
		filterChainDefinitionMap.put("/html/study/**","anon");
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
		userRealm.setCacheManager(cacheManager());
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
//		HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
		RetryLimitHashedCredentialsMatcher credentialsMatcher =new RetryLimitHashedCredentialsMatcher(cacheManager());
		// 散列算法:这里使用MD5算法;
		credentialsMatcher.setHashAlgorithmName("md5");
		// 散列的次数，比如散列两次，相当于 md5(md5(""));
		credentialsMatcher.setHashIterations(3);
		// storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
		credentialsMatcher.setStoredCredentialsHexEncoded(true);
		return credentialsMatcher;
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
		sessionManager.setGlobalSessionTimeout(1800000);
		//<!-- 删除失效的session -->
		sessionManager.setDeleteInvalidSessions(true);
		//是否定期检查Session的有效性 
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.setCacheManager(cacheManager());
		// <!-- Shiro提供SessionDAO用于会话的CRUD -->
		sessionManager.setSessionDAO(sessionDAO());
		sessionManager.getSessionListeners().add(sessionListener());
		return sessionManager;
	}
	
	@Bean
	public RedisManager redisManager(){
		org.crazycake.shiro.RedisManager redisManager = new RedisManager();
		logger.info("注入enviroment : "+enviroment.getClass().getName());
		redisManager.setHost(enviroment.getProperty("spring.redis.host"));
		redisManager.setPort(Integer.valueOf(enviroment.getProperty("spring.redis.port")));
		//jedis的超时尝试连接到redis服务器，而不是过期时间!以毫秒为单位
		redisManager.setTimeout(Integer.valueOf(enviroment.getProperty("spring.redis.timeout")));
		redisManager.setPassword(enviroment.getProperty("spring.redis.password"));
		redisManager.setDatabase(Integer.parseInt(enviroment.getProperty("spring.redis.database")));
		return redisManager;
	}

	@Bean(value = "redisSessionDAO")
	public RedisSessionDAO sessionDAO() {
		org.crazycake.shiro.RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
		//设置生成sessionId生成策略:
		//RandomSessionIdGenerator() 和 JavaUuidSessionIdGenerator()
		redisSessionDAO.setSessionIdGenerator(new RandomSessionIdGenerator());
		redisSessionDAO.setKeyPrefix(Constant.REDISDAO_LOGIN_PREFIX);
		redisSessionDAO.setKeySerializer(new StringSerializer());
		redisSessionDAO.setValueSerializer(new ObjectSerializer());
		redisSessionDAO.setRedisManager(redisManager());
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
		logger.info("注入Shrio的缓存管理器-->ehCacheManager" + RedisCacheManager.class);
		org.crazycake.shiro.RedisCacheManager cacheManager = new org.crazycake.shiro.RedisCacheManager();
		cacheManager.setRedisManager(redisManager());
		//存入redis的key的过期时间(秒为单位)
		cacheManager.setExpire(Integer.valueOf(enviroment.getProperty("spring.redis.expire.timeout")));
		cacheManager.setKeyPrefix(Constant.REDISDAO_CACHE_PREFIX);
		return cacheManager;
	}
	
	/**
	 *  kickoutSessionControlFilter 用于控制并发登录人数的 
	 * @return
	 */
//	@Bean
	public AccessControlFilter kickoutFilter() {
		logger.info("注入Shrio的一个用户只能在一个地方登录过滤器--> "+KickoutSessionControlFilter.class);
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
	
	@Bean
	public SessionListener sessionListener() {
		return new MyShiroSessionListener();
	}

	/**
	 * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
	 * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
	 *
	 * @return
	 */
	@Bean
	@DependsOn({"lifecycleBeanPostProcessor"})
	public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		advisorAutoProxyCreator.setProxyTargetClass(true);
		return advisorAutoProxyCreator;
	}
	/**
	 * AuthorizationAttributeSourceAdvisor，shiro里实现的Advisor类，
	 * 内部使用AopAllianceAnnotationsAuthorizingMethodInterceptor来拦截用以下注解的方法。
	 */

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
		return authorizationAttributeSourceAdvisor;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.enviroment=environment;
	}
}
