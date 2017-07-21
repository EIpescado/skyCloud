package org.skyCloud.common.utils;

import java.io.Serializable;

/**
 * Created by yq on 2017/06/21 17:13.
 * 下拉框数据包装类
 */
public class ComboBoxWrapper<ID,CODE,NAME> implements Serializable {
    private ID id;
    private CODE code;
    private NAME name;

    public ComboBoxWrapper() {

    }

    public ComboBoxWrapper(ID id, CODE code, NAME name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public ID getId() {
        return id;
    }

    public CODE getCode() {
        return code;
    }

    public NAME getName() {
        return name;
    }
}
