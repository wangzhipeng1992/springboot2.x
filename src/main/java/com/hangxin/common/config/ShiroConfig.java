package com.hangxin.common.config;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.hangxin.shiro.redis.RedisCacheManager;
import com.hangxin.shiro.shiro.CustomFormAuthenticationFilter;
import com.hangxin.shiro.shiro.QyDefaultWebSessionManager;
import com.hangxin.shiro.shiro.QyShiroSessionIdGenerator;
import com.hangxin.shiro.shiro.ShiroDbRealm;

@Configuration
public class ShiroConfig {
	@Value("${unlogin.url}")
	private String unLoginUrl;

	@Bean(name = "shiroRealm")
	@DependsOn("lifecycleBeanPostProcessor")
	public ShiroDbRealm getShiroRealm() {
		ShiroDbRealm realm = new ShiroDbRealm();
		return realm;
	}

	// @Bean(name = "shiroMemCacheManager")
	// @DependsOn("lifecycleBeanPostProcessor")
	// public CacheManager getMemCacheManager() {
	// CacheManager cache = new MemoryConstrainedCacheManager();
	// return cache;
	//
	// }

	@Bean(name = "shiroRedisCacheManager")
	public RedisCacheManager redisCacheManager() {
		return new RedisCacheManager();
	}

	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	@ConditionalOnMissingBean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	@Bean
	@Primary
	public SessionManager sessionManager() {
		QyDefaultWebSessionManager sessionManager = new QyDefaultWebSessionManager();
		sessionManager.setSessionDAO(enterpriseSessionDao());
		sessionManager.setCacheManager(redisCacheManager());
		return sessionManager;
	}

	/**
	 * 
	 * @description redis sessionDao <br/>
	 * 
	 * @return SessionDAO @throws
	 */
	// @Bean(name = "redisSessionDao")
	// public SessionDAO redisSessionDao() {
	// RedisSessionDAO sesisonDao = new RedisSessionDAO();
	// sesisonDao.setActiveSessionsCacheName("qy-shiro-session-cache");
	// sesisonDao.setSessionIdGenerator(new QyShiroSessionIdGenerator());
	// return sesisonDao;
	// }
	
	/**
	 * 
	 * @description EnterpriseCacheSessionDAO <br/>
	 * 
	 * @return SessionDAO @throws
	 */
	@Bean(name = "enterpriseSessionDao")
	public SessionDAO enterpriseSessionDao() {
		EnterpriseCacheSessionDAO sessionDao = new EnterpriseCacheSessionDAO();
		sessionDao.setActiveSessionsCacheName("qy-shiro-session-cache");
		sessionDao.setSessionIdGenerator(new QyShiroSessionIdGenerator());
		return sessionDao;
	}

	@Bean(name = "securityManager")
	public DefaultWebSecurityManager getDefaultWebSecurityManager(ShiroDbRealm shiroRealm,
			CacheManager shiroRedisCacheManager) {
		DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
		dwsm.setSessionManager(sessionManager());
		dwsm.setRealm(shiroRealm);
		dwsm.setCacheManager(shiroRedisCacheManager);
		return dwsm;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
			DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
		aasa.setSecurityManager(securityManager);
		return new AuthorizationAttributeSourceAdvisor();
	}

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager,
			CustomFormAuthenticationFilter authenticationFilter) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		Map<String, Filter> filters = new LinkedHashMap<>();
		filters.put("authc", new CustomFormAuthenticationFilter());
		shiroFilterFactoryBean.setFilters(filters);
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 未授权错误提示
		// shiroFilterFactoryBean.setUnauthorizedUrl("/test");
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
		// anon : 认证放行
		// authc ： 强校验
		filterChainDefinitionMap.put("/login", "authc");
		filterChainDefinitionMap.put("/logout", "logout");
		filterChainDefinitionMap.put("/ATE/**", "anon");
		filterChainDefinitionMap.put("/plugins/**", "anon");
		filterChainDefinitionMap.put("/logs", "anon");
		filterChainDefinitionMap.put("/clear", "anon");
		filterChainDefinitionMap.put("/health/**", "anon");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * @description 代理filter <br/>
	 * 
	 * @return FilterRegistrationBean @throws
	 */
	@Bean
	@DependsOn("shiroFilter")
	public FilterRegistrationBean filterRegistrationBean() {

		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
		filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
		// 该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
		filterRegistration.addInitParameter("targetFilterLifecycle", "true");
		filterRegistration.setEnabled(true);
		filterRegistration.addUrlPatterns("/*");
		return filterRegistration;
	}

}
