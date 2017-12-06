package org.skyCloud.service;

import org.skyCloud.common.utils.CodeUtils;
import org.skyCloud.entity.Menu;
import org.skyCloud.entity.MenuType;
import org.skyCloud.entity.Role;
import org.skyCloud.entity.User;
import org.skyCloud.exception.UserException;
import org.skyCloud.repository.MenuRepository;
import org.skyCloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yq on 2016/06/23 17:39.
 * 用户service
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository ;
    @Autowired
    private MenuRepository menuRepository;

    @Transactional
    public void register(User user){
       if(userRepository.countByUserName(user.getUserName()) > 0){
           throw new UserException(UserException.ErrorEnum.USER_NAME_REPEAT);
       }
        if(userRepository.countByEmail(user.getEmail()) > 0){
            throw new UserException(UserException.ErrorEnum.EMAIL_REPEAT);
        }
        if(userRepository.countByPhone(user.getPhone()) > 0){
            throw new UserException(UserException.ErrorEnum.PHONE_REPEAT);
        }
        userRepository.save(user);
    }

    /**根据邮箱密码获取用户*/
    public User findByEmailAndPassword(String email,String password){
        //sh1 加密转16进制大写
        return userRepository.findByEmailAndPassword(email,password);
    }

    /**
     * 获取用户菜单 管理员角色拥有全部菜单
     * @param userId 用户id
     */
    public List<Menu> getMenuByUserId(String userId){
        User user = userRepository.getOne(userId) ;
        if(user != null) {
            boolean isAdmin = false;
            Set<Menu> menus = new HashSet<>();
            for (Role role : user.getRoles()) {
                if ("role_admin".equalsIgnoreCase(role.getCode())) {
                    isAdmin = true;
                }
            }
            if (isAdmin) {
                menus.addAll(menuRepository.findAllByMenuTypeOrderByCodeAsc(MenuType.NavigationMenu));//所有导航菜单
            }else {
                menus.addAll(menuRepository.findAllByMenuTypeAndRolesInOrderByCodeAsc(MenuType.NavigationMenu,user.getRoles()));
            }
            return new ArrayList<Menu>(){{addAll(menus);}};
        }
        return null;
    }

    public List<Menu> getButtonByMenuId(String menuId,String userId){
        User user = userRepository.getOne(userId) ;
        if(user != null) {
            boolean isAdmin = false;
            Set<Menu> menus = new HashSet<>();
            for (Role role : user.getRoles()) {
                if ("role_admin".equalsIgnoreCase(role.getCode())) {
                    isAdmin = true;
                }
            }
            if (isAdmin) {
                //获取单个菜单下的所有按钮菜单
                menus.addAll(menuRepository.findAllByMenuTypeAndRootMenuIdOrderByCodeAsc(MenuType.ButtonMenu,menuId));
            }else {
                menus.addAll(menuRepository.findAllByMenuTypeAndRootMenuIdAndRolesInOrderByCodeAsc(MenuType.ButtonMenu,menuId,user.getRoles()));
            }
            return new ArrayList<Menu>(){{addAll(menus);}};
        }
        return null;
    }

    @Transactional
    public void updateUser(User user){
        userRepository.saveAndFlush(user);
    }

    /**
     * 明文密码加密
     * @param password 明文密码
     */
    public String encryptPassword(String password){
        //先base64加密
        password = CodeUtils.encryptBASE64(password.getBytes());
        //sha1加密转16进制 大写
        password = CodeUtils.sha1Hex(password);
        return password ;
    }

}
