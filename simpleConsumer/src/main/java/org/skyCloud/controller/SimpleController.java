package org.skyCloud.controller;

import org.skyCloud.client.SimpleClient;
import org.skyCloud.common.dataWrapper.BackResult;
import org.skyCloud.stream.SimpleSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by yq on 2017/07/04 16:07.
 */
@RestController
@RequestMapping("simple")
@RefreshScope
public class SimpleController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleClient simpleClient;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private SimpleSource simpleSource;
    @Value("${bus.test}")
    private String bus;

    @GetMapping("processMessage")
    public void processEvent() {
        simpleSource.simpleConsumer_system_output().send(
//                MessageBuilder.withPayload("{\"code\":\"1\",\"msg\":\"成功\",\"data\":[{\"code\":\"\",\"name\":\"全部\",\"tote\":74953,\"documentStatusId\":\"\"}]}")
                MessageBuilder.withPayload(new BackResult())
                        .build());
    }

    @GetMapping("listMenu")
    public Object listMenu() {
       return simpleClient.list();
    }

    @GetMapping("serviceInfo")
    public Object serviceInfo(@RequestParam String serviceName){
      return   discoveryClient.getInstances(serviceName);
    }

}
