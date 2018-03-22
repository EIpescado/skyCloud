package org.skyCloud.exception;

import org.skyCloud.common.dataWrapper.BackResult;
import org.springframework.core.NestedRuntimeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yq on 2016/06/15 10:25.
 * controller  异常回调
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BackResult defaultErrorHandler(HttpServletRequest req, Exception e){
        return  BackResult.failureBack(e.getCause().toString());
    }

    //spring 抛出的异常
    @ExceptionHandler(NestedRuntimeException.class)
    @ResponseBody
    public BackResult sqlExceptionHandler(NestedRuntimeException e){
        return  BackResult.failureBack(e.getRootCause().getMessage());
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public String defaultErrorHandler(ArithmeticException ex){
        return ex.getMessage();
    }

}
