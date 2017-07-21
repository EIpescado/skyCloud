package org.skyCloud.common.entity;

/**
 * Created by yq on 2017/03/24 10:52.
 * 行政区域级别
 */
public enum RegionLevelEum {

    PROVINCE("省"), CITY("市"), AREA("区/县");

    private String name ;

    private RegionLevelEum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
