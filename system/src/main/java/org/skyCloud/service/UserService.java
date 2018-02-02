package org.skyCloud.service;

import org.skyCloud.common.utils.CodeUtils;
import org.skyCloud.entity.Menu;
import org.skyCloud.entity.MenuTypeEnum;
import org.skyCloud.entity.Role;
import org.skyCloud.entity.User;
import org.skyCloud.exception.UserException;
import org.skyCloud.repository.MenuRepository;
import org.skyCloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.schema.Maps;

import java.util.*;
import java.util.stream.Collectors;

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
                menus.addAll(menuRepository.findAllByMenuTypeOrderByCodeAsc(MenuTypeEnum.NavigationMenu));//所有导航菜单
            }else {
                menus.addAll(menuRepository.findAllByMenuTypeAndRolesInOrderByCodeAsc(MenuTypeEnum.NavigationMenu,user.getRoles()));
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
                menus.addAll(menuRepository.findAllByMenuTypeAndRootMenuIdOrderByCodeAsc(MenuTypeEnum.ButtonMenu,menuId));
            }else {
                menus.addAll(menuRepository.findAllByMenuTypeAndRootMenuIdAndRolesInOrderByCodeAsc(MenuTypeEnum.ButtonMenu,menuId,user.getRoles()));
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

    public static void main(String[] args) {
        List<Menu> menus = new ArrayList<>();
        List<String> list = new ArrayList<>();
        for (int x = 0 ; x < 5 ; x ++ ){
            Menu m = new Menu();
            m.setName(x + "");
            menus.add(m);
            list.add(x + "");
        }
        Menu m = new Menu();
        m.setName("1");
        menus.add(m);
        list.add("1");
//        List<String> x = menus.stream().map(Menu::getName).filter(f -> Integer.parseInt(f) > 3 ).collect(Collectors.toList());
//        List<String> x = menus.stream().filter(j->!"1".equals(j.getName())).map(Menu::getName).distinct().collect(Collectors.toList());
//        x.forEach(System.out::println);
//        Map<String,List<Menu>> map = menus.stream().collect(Collectors.groupingBy(Menu::getName));
//        Map<String,List<Menu>> map = menus.stream().collect(Collectors.groupingByConcurrent(Menu::getName));
//        map.entrySet().forEach(entry ->{
//            System.out.println(entry.getKey() + "___" + entry.getValue().size());
//        });
//        list.stream().distinct().collect(Collectors.toList()).forEach(System.out::println);

        List<Map<String,String>> mapList = new ArrayList<>();
        for (int x = 0 ; x < 5 ; x ++ ){
            Map<String,String> map = new HashMap<>();
            map.put("test",x + "");
            mapList.add(map);
        }
        Map<String,String> sks = new HashMap<>();
        sks.put("test","1");
        mapList.add(sks);
        mapList.stream().map(map -> map.get("test")).distinct().collect(Collectors.toList()).forEach(System.out::println);

        String ss = "402880885fc7b779015fd8d266c413a5";

    }

    public static void haha(){
        hehe();
    }

    public static void hehe(){
        System.out.println("test");

        System.out.println("test....");
    }
}
