package org.skyCloud.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yq
 * @date 2018/02/01 9:29
 * @description
 * @since V1.0.0
 */
public class Utils {

    /**
     * 获取当前正在执行最上层方法名称
     * @return
     */
    public static String getCurrentExecuteMethodName(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stackTrace[stackTrace.length - 1];
        return e.getMethodName();
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0 ; i < 6 ; i ++){
            list.add(i);
        }
        list.forEach(System.out::print);
        //1.2
        list.subList(1,2).forEach(System.out::print);
        System.out.println();
        int x = 2/3 + 1  ;
        System.out.println(x);
    }
}
