package org.skyCloud.common.utils;

/**
 * Created by yq on 2017/12/18 15:31.
 * 先决条件
 */
public class Preconditions {

    private Preconditions() {
    }

    /**
     * 检查参数
     * @param expression 布尔表达式结果
     */
    public static void checkArgument(boolean expression) {
        if(!expression) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 检查参数
     * @param expression 布尔表达式结果
     */
    public static void checkArgument(boolean expression,String errorMessage) {
        if(!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static <T> T checkNotNull(T reference) {
        if(reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if(reference == null) {
            throw new NullPointerException(errorMessage);
        } else {
            return reference;
        }
    }

}
