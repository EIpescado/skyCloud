package org.skyCloud.service;

import org.skyCloud.entity.Menu;
import org.skyCloud.repository.MenuRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by yq on 2017/08/17 9:47.
 * 菜单service
 */
@Service
public class MenuService {

    private final Logger logger = LoggerFactory.getLogger(MenuService.class);

    @Autowired
    private MenuRepository menuRepository;

    public List<Menu> list(){
        return  menuRepository.findAll();
    }

}
