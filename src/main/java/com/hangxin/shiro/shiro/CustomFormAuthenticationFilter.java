package com.hangxin.shiro.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hangxin.basic.Result;
import com.hangxin.basic.ResultCode;
import com.hangxin.util.JsonHelper;

@Component
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@Value("${unlogin.url}")
	private String unLoginUrl;

	private static final String SESSION_ID_NAME = "_sid";
	private static final String APPLICATION = "application";

	public String getUnLoginUrl() {
		return unLoginUrl;
	}

	public void setUnLoginUrl(String unLoginUrl) {
		this.unLoginUrl = unLoginUrl;
	}

	@Override
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		logger.info("访问传入参数  request={} ", JsonHelper.parseToJson(request));
		// 取出验证码
		String _sid = request.getParameter(SESSION_ID_NAME);
		String application = request.getParameter(APPLICATION);
		if (StringUtils.isNotBlank(_sid) || StringUtils.isNotBlank(application)) {
			WebUtils.issueRedirect(request, response, unLoginUrl);
		} else {
			super.redirectToLogin(request, response);
		}

	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {

		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//		String head = httpServletResponse.getHeader("Access-Control-Allow-Origin");
//		logger.debug("请求头 head={}", head);
//		if (StringUtils.isBlank(head))
//			httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
//		logger.debug("处理后请求头 head={}", httpServletResponse.getHeader("Access-Control-Allow-Origin"));
		// 取出验证码
		String _sid = httpServletRequest.getParameter(SESSION_ID_NAME);
		String application = httpServletRequest.getParameter(APPLICATION);
		if (StringUtils.isNotBlank(_sid) || StringUtils.isNotBlank(application)) {

			httpServletResponse.setContentType("application/json;charset=utf-8");
			Result result = Result.result(ResultCode.RESULT__UNLOGIN);
			httpServletResponse.getWriter().write(JSON.toJSONString(result));
			return false;
		} else {
			return super.onAccessDenied(request, response);
		}
	}

}
