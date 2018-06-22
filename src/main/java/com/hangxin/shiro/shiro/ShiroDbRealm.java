package com.hangxin.shiro.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;


/**
 * @Description: shiro数据库认证授权类
 */
public class ShiroDbRealm extends AuthorizingRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		String accountName = null;
		String application = null;
		if (authcToken instanceof ApplicationToken) {
			ApplicationToken token = (ApplicationToken) authcToken;
			accountName = token.getUsername();
			application = token.getApplication();
		} else {
			UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
			accountName = token.getUsername();

		}
		SessionUser sessionUser = new SessionUser();
		return new SimpleAuthenticationInfo(sessionUser, "123456", getName());

	}

	/**
	 * <p>
	 * Title: doGetAuthorizationInfo
	 * </p>
	 * <p>
	 * Description: 授权查询回调函数 ，进行鉴权但缓存中无用户的授权信息时调用
	 * </p>
	 * 
	 * @param arg0
	 * @return
	 * @see org.apache.shiro.realm.AuthorizingRealm#doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		SessionUser sessionUser = (SessionUser) principals.fromRealm(getName()).iterator().next();
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
			info.addStringPermission("*");
		return info;
	}

	/**
	 * 更新用户授权信息缓存.
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}
}
