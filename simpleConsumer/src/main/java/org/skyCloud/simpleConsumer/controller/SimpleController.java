package org.skyCloud.simpleConsumer.controller;

import org.skyCloud.simpleConsumer.client.SimpleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yq on 2017/07/04 16:07.
 */
@RestController
public class SimpleController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleClient simpleClient;
    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping(value = "/add" ,method = RequestMethod.GET)
    public Integer add(@RequestParam Integer a, @RequestParam Integer b) {
        logger.info("开始调用 {} 服务",discoveryClient.getLocalServiceInstance().getServiceId());
       return simpleClient.add(a,b);
    }

}
