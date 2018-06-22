package com.hangxin.shiro.shiro;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QyDefaultWebSessionManager extends DefaultWebSessionManager {

    // 存放登陆信息
    public static final ConcurrentHashMap<String, String> loginCache = new ConcurrentHashMap<>();

    private static final Logger log = LoggerFactory.getLogger(QyDefaultWebSessionManager.class);
    private static final String SESSION_ID_NAME = "_sid";
    private static final String APPLICATION = "application";
    private static final String IS_SAVE_COOKIE = "_cookie";

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        // 如果参数中包含“__sid”参数，则使用此sid会话。
        // 例如：http://localhost/project?__sid=xxx&__cookie=true
        // 其实这里还可以使用如下参数：cookie中的session名称：如：JSESSIONID=xxx,路径中的
        // ;JESSIONID=xxx，但建议还是使用 __sid参数。
        String sid = request.getParameter(SESSION_ID_NAME);
        String application = request.getParameter(APPLICATION);
        if (StringUtils.isNotBlank(sid)) {
            log.info("_sid:{}", sid);
            // 是否将sid保存到cookie，浏览器模式下使用此参数。
            if (WebUtils.isTrue(request, IS_SAVE_COOKIE)) {
                HttpServletRequest rq = (HttpServletRequest) request;
                HttpServletResponse rs = (HttpServletResponse) response;
                Cookie template = getSessionIdCookie();
                Cookie cookie = new SimpleCookie(template);
                cookie.setValue(sid);
                cookie.saveTo(rq, rs);
            }
            // 设置当前session状态
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
                    ShiroHttpServletRequest.URL_SESSION_ID_SOURCE); // session来源与url
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sid);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sid;
        } else if (StringUtils.isNotBlank(application)) {
            log.info("application:{}", application);
            String sessionId = "";
            if (!loginCache.containsKey(application)) {
                log.error("loginCache not find sid via application {}", application);

            } else {
                sessionId = loginCache.get(application);
            }

            // 是否将sid保存到cookie，浏览器模式下使用此参数。
            if (WebUtils.isTrue(request, IS_SAVE_COOKIE)) {
                HttpServletRequest rq = (HttpServletRequest) request;
                HttpServletResponse rs = (HttpServletResponse) response;
                Cookie template = getSessionIdCookie();
                Cookie cookie = new SimpleCookie(template);
                cookie.setValue(sessionId);
                cookie.saveTo(rq, rs);
            }
            // 设置当前session状态
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE,
                    ShiroHttpServletRequest.URL_SESSION_ID_SOURCE); // session来源与url
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sessionId;

        } else {
            return super.getSessionId(request, response);
        }
    }

}
