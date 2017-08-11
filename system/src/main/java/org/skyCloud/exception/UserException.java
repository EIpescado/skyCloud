package org.skyCloud.exception;

/**
 * Created by yq on 2017/06/02 15:26.
 * 用户相关异常
 */
public class UserException extends RuntimeException {

    public UserException(ErrorEnum errorEnum) {
        super(errorEnum.toString());
    }

    //异常明细
    public enum ErrorEnum{
        USER_NAME_REPEAT("用户名重复"),
        EMAIL_REPEAT("邮箱重复"),
        PHONE_REPEAT("手机号重复");

        private final String description ;
        ErrorEnum(String description) {
            this.description = description ;
        }

        @Override
        public String toString() {
            return description ;
        }
    }
}

