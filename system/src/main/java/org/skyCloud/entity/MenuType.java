package org.skyCloud.entity;

/**
 * Created by yq on 2017/05/27 16:16.
 * 菜单类型
 */
public enum MenuType {

    NavigationMenu("导航菜单"),
    ButtonMenu("按钮菜单");

    private String name ;

    private MenuType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
