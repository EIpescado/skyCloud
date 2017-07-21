package org.skyCloud.common.exception;

/**
 * Created by yq on 2017/03/23 10:31.
 * 异常返回类
 */
public class ExceptionResponse {

    private String message;//异常信息
    private String code; //异常编码

    public ExceptionResponse(String code, String message){
        this.message = message;
        this.code = code;
    }

    public static ExceptionResponse create(String code, String message){
        return new ExceptionResponse(code, message);
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
