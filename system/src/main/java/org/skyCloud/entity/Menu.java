package org.skyCloud.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.skyCloud.common.base.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * Created by yq on 2017/05/27 16:00.
 * 菜单
 */
@Entity
@Table
public class Menu extends BaseModel {

    //菜单名称
    @Column(name = "MENU_NAME", nullable = false, unique = false,length = 40)
    private String name ;

    //菜单编码
    @Column(name = "MENU_CODE", nullable = false, unique = true,length = 40)
    private String code ;

    //菜单级别
    private Integer level = 0 ;

    //菜单url
    private String url ;

    //上级菜单
    private String rootMenuId ;

    //样式
    private String style ;

    //按钮事件
    private String event ;

    //菜单类型
    @Enumerated(EnumType.STRING) //枚举映射
    private MenuType menuType = MenuType.NavigationMenu ;

    @ManyToMany(mappedBy = "menus")
    @JsonIgnore
    private Set<Role> roles ;

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRootMenuId() {
        return rootMenuId;
    }

    public void setRootMenuId(String rootMenuId) {
        this.rootMenuId = rootMenuId;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public MenuType getMenuType() {
        return menuType;
    }

    public void setMenuType(MenuType menuType) {
        this.menuType = menuType;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;
        if(getId() == null ||  "".equals(getId())) return false;
        if(menu.getId() == null ||  "".equals(menu.getId())) return false;
        return getId().equals(menu.getId());

    }

    @Override
    public int hashCode() {
        int result = getCode().hashCode();
        result = 31 * result + getLevel().hashCode();
        result = 31 * result + getUrl().hashCode();
        return result;
    }

}
