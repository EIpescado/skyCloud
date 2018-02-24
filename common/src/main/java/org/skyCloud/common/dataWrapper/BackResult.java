package org.skyCloud.common.dataWrapper;

/**
 * Created by yq on 2017/01/03 16:44.
 * 返回结果
 */
public class BackResult {
    private static final String FAILURE_CODE = "0";
    private static final String SUCCESS_CODE = "1";
    private static final String SUCCESS_MESSAGE = "success";

    /**返回结果 编码 1：成功 0：失败*/
    private String code = SUCCESS_CODE ;
    /**返回结果 描述信息*/
    private String message = SUCCESS_MESSAGE;
    /**返回结果*/
    private Object res = "" ;

    public BackResult() {
    }

    public BackResult(String code, String message, Object res) {
        this.code = code;
        this.message = message;
        this.res = res;
    }

    /**判断返回是否成功*/
    public Boolean succeeded(){
        return code.equals(SUCCESS_CODE);
    }

    public static Boolean succeeded(BackResult backResult){
        return backResult != null && backResult.succeeded();
    }

    /**返回成功*/
    public  static BackResult successBack(Object res){
        return new BackResult(SUCCESS_CODE,SUCCESS_MESSAGE,res);
    }

    /**返回失败*/
    public static BackResult failureBack(String message){
        return new BackResult(FAILURE_CODE,message,null);
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

    public Object getRes() {
        return res;
    }

    public void setRes(Object res) {
        this.res = res;
    }
}
