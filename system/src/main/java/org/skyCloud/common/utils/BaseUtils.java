package org.skyCloud.common.utils;

import org.skyCloud.common.constants.RequestConstant;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Created by yq on 2017/12/18 17:20.
 * 基础工具类 每个服务所需不一样
 */
public class BaseUtils {

    private BaseUtils(){

    }

    /**
     * 直接从request头中获取token 取对应属性值
     * @param secret 密钥
     * @param fieldName 属性名称
     */
    public static String getFieldValueFromToken(String secret,String fieldName) {
        return TokenUtil.getFieldValue(getToken(),secret,fieldName);
    }

    /**
     * 从request请求头中获取token
     */
    public static String getToken(){
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取请求头中的token
        return StringUtils.null2EmptyWithTrim(attr.getRequest().getHeader(RequestConstant.TOKEN));
    }
}
