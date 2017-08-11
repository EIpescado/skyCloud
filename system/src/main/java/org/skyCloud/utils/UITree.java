package org.skyCloud.utils;


import org.skyCloud.common.utils.StringUtils;
import org.skyCloud.entity.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * add by yq 2017年7月5日 17:17:32
 * 构建UI树
 */
public class UITree {


    private  static  Integer count = 0;

    private String id; //id
    private String name; //名称
    private String url; //url
    private String style ; //样式
    private String parentId;//父菜单
    private Integer level ;//菜单级别
    private boolean leaf; //是否子菜单
    private String event; //按钮菜单 函数
    private List<UITree> children; //子菜单

    public UITree() {
    }

    public UITree(String id, boolean leaf, String style, String url, String name, String parentId, Integer level, String event) {
        this.id = id;
        this.leaf = leaf;
        this.style = style;
        this.url = url;
        this.name = name;
        this.parentId = parentId ;
        this.level = level ;
        this.event = event ;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public List<UITree> getChildren() {
        return children;
    }

    public void setChildren(List<UITree> children) {
        this.children = children;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    //获取菜单子菜单
    public static List<UITree> subMenu(UITree r,List<Menu> menus){
         List<UITree> sub = new ArrayList<>(menus.size());
        //获取菜单r的子菜单
        for(Menu menu : menus){
            if(r.getId().equals(menu.getRootMenuId())){
                UITree tree = new UITree(menu.getId(),
                                        StringUtils.isNotEmpty(menu.getRootMenuId()),
                                        menu.getStyle(),
                                        menu.getUrl(),
                                        menu.getName(),
                                        menu.getRootMenuId(),count,menu.getEvent());
                sub.add(tree);
            }
        }
        return  sub;
    }

    /**
     * 构建单个菜单树
     * @param tree 根菜单
     * @param menus 所有菜单
     * @return
     */
    public static UITree buildTree(UITree tree,List<Menu> menus){
        count ++ ;
        List<UITree> sub = subMenu(tree,menus);
        for(UITree child:sub){
            UITree t = buildTree(child,menus);
            if(tree.children != null){
                tree.children.add(t);
            }else {
                tree.children = new ArrayList<UITree>(){
                    {
                        add(t) ;
                    }
                };
            }
        }
        return  tree;
    }

    /**
     * 构建所有菜单数
     * @param menus
     */
    public static List<UITree> buildTree(List<Menu> menus){
        List<UITree> uiTreeList = new ArrayList<>(menus.size());

        menus.stream().forEach(r -> {
            //根菜单
            if(StringUtils.isEmpty(r.getRootMenuId())){
                UITree tree = new UITree(r.getId(),
                                        StringUtils.isNotEmpty(r.getRootMenuId()),
                                        r.getStyle(),
                                        r.getUrl(),
                                        r.getName(),
                                        r.getRootMenuId(),count,r.getEvent());
                uiTreeList.add(buildTree(tree,menus));
                count = 0;
            }
        });
        return  uiTreeList;
    }
}
