package org.skyCloud.common.utils;

import java.util.Collection;

/**
 * Created by yq on 2017/08/16 14:41.
 * 集合工具类
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection){
        return collection != null && ! collection.isEmpty();
    }
}
