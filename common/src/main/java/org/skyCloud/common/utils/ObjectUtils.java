package org.skyCloud.common.utils;

/**
 * Created by yq on 2017/06/22 15:24.
 * Object工具类
 */
public class ObjectUtils {

    private ObjectUtils(){

    }

    public static boolean isNull(Object obj){
       return obj == null || obj.toString().trim().length() == 0 ||"null".equalsIgnoreCase(obj.toString()) ;
    }
}
