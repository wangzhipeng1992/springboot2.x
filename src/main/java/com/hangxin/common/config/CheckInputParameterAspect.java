package com.hangxin.common.config;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hangxin.util.IllegalStrFilterUtil;

@Component
@Aspect
public class CheckInputParameterAspect {

    private static final Logger logger = LoggerFactory.getLogger(CheckInputParameterAspect.class);

    // 存在SQL注入风险
    private static final String IS_SQL_INJECTION = "输入参数存在SQL注入风险";

    private static final String UNVALIDATED_INPUT = "输入参数含有非法字符";

    private static final String ERORR_INPUT = "输入的参数非法";

    /**
     * 定义切入点:拦截controller层所有方法
     */
    @Pointcut("execution(* com.qiyou.controller..*(..))")
    public void params() {
    }

    /**
     * 定义环绕通知
     * 
     * @param joinPoint
     * @throws Throwable
     */
    @Around("params()")
    public Object doBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object[] args = joinPoint.getArgs();// 参数
        String str = String.valueOf(args);

        if (!IllegalStrFilterUtil.sqlStrFilter(str)) {
            logger.info(IS_SQL_INJECTION);
            new RuntimeException(ERORR_INPUT);
        }
        if (!IllegalStrFilterUtil.isIllegalStr(str)) {
            logger.info(UNVALIDATED_INPUT);
            new RuntimeException(ERORR_INPUT);
        }
        Object result = joinPoint.proceed();
        logger.info("当前调用接口-[" + request.getRequestURL() + "]");
        return result;
    }

}
