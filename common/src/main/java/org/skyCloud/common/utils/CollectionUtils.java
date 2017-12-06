package org.skyCloud.common.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yq on 2017/08/16 14:41.
 * 集合工具类
 */
public class CollectionUtils {

    private CollectionUtils(){

    }

    public static boolean isEmpty(Collection collection){
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection){
        return collection != null && ! collection.isEmpty();
    }

    public static boolean isEmpty(Map map){
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map){
        return map != null && ! map.isEmpty();
    }

    /**
     * 返回集合中第一个equals的值
     * @param collection 集合
     * @param e 过滤的元素
     * @param <E> 类型
     */
    public static <E> E findFirstEquals(Collection<E> collection,E e) {
        if(isNotEmpty(collection)){
            Optional<E> optional = collection.stream().filter(obj ->
                  obj.equals(e)
            ).findFirst();
            return optional.isPresent() ? optional.get() : null;
        }
        return null;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("3");
        System.out.println("结果:" + findFirstEquals(list,"5"));
        List<String> data = list.stream()
                .filter(article -> article.contains("2"))
                .collect(Collectors.toList());
        System.out.println(data.size() + " " + data.get(0));
    }
}
