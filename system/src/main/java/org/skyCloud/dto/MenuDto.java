package org.skyCloud.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.skyCloud.entity.Menu;
import org.skyCloud.entity.MenuTypeEnum;

import java.util.Date;

/**
 * Created by yq on 2017/06/05 9:32.
 * 菜单DTO
 */
public class MenuDto {

    private String  id ;

    private String name ;

    private String url ;

    private String style ;

    private MenuTypeEnum menuType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreated ;

    private Boolean enabled ;

    private String code ;

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public MenuTypeEnum getMenuType() {
        return menuType;
    }

    public void setMenuType(MenuTypeEnum menuType) {
        this.menuType = menuType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Menu toMenu(){
        Menu menu = new Menu();
        menu.setName(name);
        menu.setUrl(url);
        menu.setStyle(style);
        menu.setMenuType(menuType);
        return menu ;
    }
}
