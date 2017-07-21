package org.skyCloud.common.dataWrapper;

import java.io.Serializable;

/**
 * Created by yq on 2017/06/21 16:59.
 * 下拉选择包装
 */
public class KeyValue<K,V> implements Serializable {
    private K key;
    private V value;

    public KeyValue() {

    }

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
