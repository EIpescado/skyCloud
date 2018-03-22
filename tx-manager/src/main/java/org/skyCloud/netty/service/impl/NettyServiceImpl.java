package org.skyCloud.netty.service.impl;

import org.skyCloud.netty.service.IActionService;
import org.skyCloud.netty.service.NettyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * create by lorne on 2017/11/11
 */
@Service
public class NettyServiceImpl implements NettyService{

    @Autowired
    private ApplicationContext spring;

    @Override
    public IActionService getActionService(String action) {
        return spring.getBean(action,IActionService.class);
    }
}
