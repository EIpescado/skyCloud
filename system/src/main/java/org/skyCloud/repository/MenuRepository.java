package org.skyCloud.repository;

import org.skyCloud.common.base.BaseRepository;
import org.skyCloud.entity.Menu;
import org.skyCloud.entity.MenuType;
import org.skyCloud.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by yq on 2017/05/27 16:11.
 * 菜单
 */
@Repository
public interface MenuRepository extends BaseRepository<Menu,String> {

    List<Menu> findAllByMenuTypeOrderByCodeAsc(MenuType menuType);

    List<Menu> findAllByMenuTypeAndRootMenuIdOrderByCodeAsc(MenuType menuType,String rootMenuId);

    //根据角色in和菜单类型获取菜单
    List<Menu> findAllByMenuTypeAndRolesInOrderByCodeAsc(MenuType menuType,Collection<Role> roles);

    //根据角色in获取菜单属于用户角色的菜单
    List<Menu> findAllByMenuTypeAndRootMenuIdAndRolesInOrderByCodeAsc(MenuType menuType,String rootMenuId,Collection<Role> roles);
}
