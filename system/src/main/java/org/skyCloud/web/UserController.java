package org.skyCloud.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.skyCloud.common.base.BaseController;
import org.skyCloud.common.constants.TokenConstant;
import org.skyCloud.common.dataWrapper.BackResult;
import org.skyCloud.common.utils.StringUtils;
import org.skyCloud.common.utils.TokenUtil;
import org.skyCloud.common.utils.UITree;
import org.skyCloud.dto.UserRegister;
import org.skyCloud.entity.Menu;
import org.skyCloud.entity.User;
import org.skyCloud.exception.UserException;
import org.skyCloud.service.MessageService;
import org.skyCloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yq on 2017/08/11 18:08.
 * 用户相关
 */
@RestController
@RequestMapping("user")
@Api(description = "用户相关")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    public BackResult register(@ModelAttribute @Valid UserRegister userRegister) throws UserException {
        String password = userRegister.getPassword() ;
        password = userService.encryptPassword(password);
        userRegister.setPassword(password);
        userService.register(userRegister.toUser());
        return new BackResult();
    }

    @ApiOperation(value = "登录 返回token等业务信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "帐号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String"),
    })
    @PostMapping(value = "/login")
    public BackResult login(@RequestParam(name = "email") String email,@RequestParam(name = "password") String password){
        User user =  userService.findByEmailAndPassword(email,userService.encryptPassword(password));
        String msg ;
        if(user != null){
            if(!user.getEnabled()){
                msg = messageService.getMessage("user.email.disabled");
            }else {
                //存入token中的业务所需用户数据
                Map<String,Object> map = new HashMap<String,Object>(){
                    {
                        put(TokenConstant.ID,user.getId());//用户id
                        put(TokenConstant.EMAIL,user.getEmail());//用户帐号
                        put(TokenConstant.PHONE,user.getPhone());//用户手机
                        put(TokenConstant.USER_NAME,user.getUserName());//用户名称
                    }
                };
                //开始生成token
                String token =  TokenUtil.getToken(user.getEmail(),tokenExpireTime,tokenSecret,map);
                Map<String,String> data = new HashMap<String,String>(){
                    {
                        put("id",user.getId());//用户id
                        put("email",user.getEmail());//用户帐号
                        put("userName",user.getUserName());//用户名称
                        put("token",token);//token
                    }
                };
                return BackResult.successBack(data);
            }
        }else {
            msg = messageService.getMessage("user.email.error");
        }
        return BackResult.failureBack(msg);
    }

    @ApiOperation(value = "获取用户菜单",notes = "根据用户请求头token获取用户菜单")
    @GetMapping("/menus")
    public BackResult menus(){
        String id = TokenUtil.getFieldValue(tokenSecret,"id");//用户id
        if(StringUtils.isEmpty(id)){
            return  BackResult.failureBack(messageService.getMessage("user.login.expire"));
        }else {
            List<Menu> menuList = userService.getMenuByUserId(id);
            List<UITree> tree = UITree.buildTree(menuList);
            return BackResult.successBack(tree);
        }
    }

    @ApiOperation(value = "登出 存入redis 若存在则表示失效")
    @GetMapping("/loginOut")
    public BackResult loginOut(){
        String token = TokenUtil.getToken();
        if(StringUtils.isNotEmpty(token)){
            try{
                //保存到session redis
                ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
                valueOps.set(token, token);
                redisTemplate.expire(token, 1, TimeUnit.DAYS);//设置key失效时间，失效时间单位：天
            }catch (Exception e){
                //redis 异常不影响操作
                return new BackResult();
            }
        }
        return new BackResult();
    }

    @ApiOperation(value = "变更密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "帐号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String")
    })
    @PostMapping("/changePassword")
    public BackResult changePassword(@RequestParam(name = "email") String email, @RequestParam(name = "oldPassword") String oldPassword, @RequestParam(name = "newPassword") String newPassword){
        User user =  userService.findByEmailAndPassword(email,userService.encryptPassword(oldPassword));
        if(user != null){
            String password = userService.encryptPassword(newPassword);
            user.setPassword(password);
            userService.updateUser(user);
            return new BackResult();
        }else {
            return BackResult.failureBack(messageService.getMessage("user.email.error"));
        }
    }

    @ApiOperation(value = "获取用户的单个菜单拥有按钮",notes = "根据用菜单ID获取页面按钮")
    @RequestMapping(value="/menu/{id}/buttons",method = RequestMethod.GET)
    @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "String")
    public BackResult getButtonByMenuId(@PathVariable(name = "id")String id){
        String userId = TokenUtil.getFieldValue(tokenSecret,TokenConstant.ID);//用户id
        if(StringUtils.isEmpty(userId)){
            return  BackResult.failureBack(messageService.getMessage("user.login.expire"));
        }else {
            List<Menu> menuList = userService.getButtonByMenuId(id,userId);
            List<UITree> tree = UITree.buildTree(menuList);
            return BackResult.successBack(tree);
        }
    }
}
