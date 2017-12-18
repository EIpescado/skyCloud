package org.skyCloud.common.utils;

import org.skyCloud.common.dataWrapper.KeyValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yq on 2017/07/06 17:14.
 * 枚举工具类
 */
public class EnumUtils {

    private EnumUtils(){

    }
    //枚举类型参数路径
    private static final String ENUM_PACKAGE_PATH = "org.skeCloud.common.enums.";
    //获取value方法名
    private static final String GET_VALUE_METHOD_NAME = "getValue";
    //获取info方法名
    private static final String GET_INFO_METHOD_NAME = "getInfo";

    /**
     * 枚举转下拉框
     * @param clazz 枚举类型class
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static List<KeyValue<String,String>> enum2KeyValue(Class<? extends Enum> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        List<KeyValue<String,String>> list = new ArrayList<KeyValue<String, String>>();
        Enum<?>[] enums =  clazz.getEnumConstants();
        Method getValue = clazz.getMethod(GET_VALUE_METHOD_NAME);
        Method getInfo =  clazz.getMethod(GET_INFO_METHOD_NAME);
        for (Enum<?> e: enums){
            KeyValue<String,String> keyValue = new KeyValue<String, String>(getValue.invoke(e).toString(),getInfo.invoke(e).toString());
            list.add(keyValue);
        }
        return  list ;
    }

    public static List<KeyValue<String,String>> enum2KeyValue(String name){
        List<KeyValue<String,String>> list = null;
        Class clazz = null;
        try {
            clazz = Class.forName( ENUM_PACKAGE_PATH + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(clazz == null) return null;
        try {
            list =  enum2KeyValue(clazz) ;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
       return list ;
    }

    public enum AccountDirection {
        DEBIT("DEBIT","借方"), CREDIT("CREDIT","贷方");


        private final String value;
        private final String info;

        private AccountDirection(String value, String info) {
            this.value = value;
            this.info = info;
        }

        public String getInfo() {
            return info;
        }

        public String getValue() {
            return value;
        }

    }

    public static void main(String[] args) throws Exception{
        System.out.println(enum2KeyValue(AccountDirection.class));
    }
}
