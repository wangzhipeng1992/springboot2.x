package com.hangxin.common.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 拦截器
 * @author Lenovo
 *
 */

public class ServerInterceptor implements HandlerInterceptor {
	private static final Logger log = LoggerFactory.getLogger(ServerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
//      处理请求跨域处理	    
		long time = System.currentTimeMillis();
		request.setAttribute("startTime", time);
//		System.out.println(">>>1MyInterceptor1>>>>>>>在请求处理之前进行调用（Controller方法调用之前）");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		long time = System.currentTimeMillis();
		time = time - Long.valueOf(String.valueOf(request.getAttribute("startTime")));

		if (time > 200) {
			log.warn("method to detect timeout for " + request.getRequestURI() + ", and the execution time is" + time
					+ "  ms");
		}
		// System.out.println(">>>MyInterceptor1>>>>>>>请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// System.out.println(">>>MyInterceptor1>>>>>>>在整个请求结束之后被调用，也就是在DispatcherServlet
		// 渲染了对应的视图之后执行（主要是用于进行资源清理工作）");
	}

}