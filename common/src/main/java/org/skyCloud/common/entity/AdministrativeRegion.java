package org.skyCloud.common.entity;


/**
 * Created by yq on 2017/03/23 17:53.
 * 行政区域
 */
public class AdministrativeRegion{

    private String name ;
    private String code ;
    private String province; //该行政区所属省 code
    private String city ;//该行政区所属市 code
    private RegionLevelEum regionLevel ; //行政区域等级

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public RegionLevelEum getRegionLevel() {
        return regionLevel;
    }

    public void setRegionLevel(RegionLevelEum regionLevel) {
        this.regionLevel = regionLevel;
    }

}
