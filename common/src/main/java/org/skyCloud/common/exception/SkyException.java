package org.skyCloud.common.exception;

/**
 * Created by yq on 2017/03/23 10:20.
 * 自定义异常类
 */
public class SkyException extends RuntimeException{

    private ExceptionResponse er;

    public SkyException(String code , String message) {
        er = ExceptionResponse.create(code,message);
    }

}
