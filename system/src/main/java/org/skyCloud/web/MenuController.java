package org.skyCloud.web;

import org.skyCloud.common.dataWrapper.BackResult;
import org.skyCloud.entity.Menu;
import org.skyCloud.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yq on 2017/08/17 9:34.
 * menu
 */
@RestController
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuService menuService ;

    @Value("${bus.test}")
    private String bus;

    @GetMapping
    public BackResult list(){
        System.out.println(bus);
       return  BackResult.successBack(menuService.list());
    }

}
