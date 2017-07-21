package org.skyCloud.common.dataWrapper;

/**
 * Created by yq on 2017/06/08 9:33.
 * 返回结果
 */
public class Result<T> {

    private static final String FAILURE_CODE = "0";
    private static final String SUCCESS_CODE = "1";
    private static final String SUCCESS_MESSAGE = "success";

    private String code  ;// 1：成功 0：失败
    private String message ;
    private T res   ;//返回对象

    public Result() {
        this(SUCCESS_CODE,SUCCESS_MESSAGE,null);
    }

    public Result(String code, String message, T res) {
        this.code = code;
        this.message = message;
        this.res = res;
    }

    //返回成功
    public Result(T res){
        this.code = SUCCESS_CODE;
        this.message = SUCCESS_MESSAGE;
        this.res = res;
    }

    //返回失败
    public Result(String message){
        this.code = FAILURE_CODE;
        this.message = message;
        this.res = null;
    }

    //判断返回是否成功
    public Boolean succeeded(){
        return code.equals(SUCCESS_CODE);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getRes() {
        return res;
    }

    public void setRes(T res) {
        this.res = res;
    }

}
