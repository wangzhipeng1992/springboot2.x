package com.hangxin.common.config;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hangxin.basic.Result;
import com.hangxin.basic.ResultCode;
import com.hangxin.util.RequestUtil;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

/**
 * 捕捉过滤全局异常
 * @author wang
 * @since 2018-05-30 18:11
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        String ip = RequestUtil.getRemoteAddr(request);
        String uri = RequestUtil.trimURI(request);
        logger.error(String.format("ip地址%s,访问地址%s", ip, uri), exception);
        Result result = Result.result(ResultCode.RESULT__ERROR_2);
        // http请求方法异常，抛出异常信息;request绑定参数异常，抛出异常信息;servlet异常抛出
        exception.printStackTrace();
        logger.error("访问出现异常  {}", exception);
        if (exception instanceof ServletException) {
            result.setResultDesc(exception.getLocalizedMessage());
        } else if (exception instanceof HttpMessageConversionException) { // request body错误
            String desc = exception.getLocalizedMessage();
            if (desc != null && desc.contains(":")) {
                desc = desc.substring(0, desc.indexOf(":"));
            }
            result.setResultDesc(desc);
        } else if (exception instanceof BindException) {
            BindException bindException = (BindException) exception;
            result.setResultDesc(bindException.getFieldError().getDefaultMessage());
        } else if (exception instanceof IllegalArgumentException) {
            IllegalArgumentException illegalArgument = (IllegalArgumentException) exception;
            result.setResultDesc(illegalArgument.getMessage());
        } else if (exception instanceof SQLException || exception.getCause() instanceof SQLException
                || exception instanceof MySQLSyntaxErrorException) {
            result.setResultDesc("db错误");
        } else {
            result.setResultDesc("System exception, please call us");
        }
        return result;
    }
}
