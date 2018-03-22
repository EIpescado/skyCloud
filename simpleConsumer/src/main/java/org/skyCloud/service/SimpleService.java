package org.skyCloud.service;

import com.codingapi.tx.annotation.TxTransaction;
import com.netflix.discovery.shared.Application;
import org.skyCloud.client.SimpleClient;
import org.skyCloud.common.utils.CollectionUtils;
import org.skyCloud.dto.UserRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author yq
 * @date 2018/03/20 11:13
 * @description
 * @since V1.0.0
 */
@Service
public class SimpleService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpleClient simpleClient;
    @Autowired
    private DiscoveryClient discoveryClient;

    @Transactional
    @TxTransaction(isStart = true,rollbackFor = Exception.class)
    public void test(){
        UserRegister user = new UserRegister();
        user.setEmail("3069@qq.com");
        user.setPhone("1265161");
        user.setPassword("1616161");
        user.setUserName("test9527");
        simpleClient.register2();
        throw new RuntimeException("cajcdoajojap");
    }

    @Transactional
    public void test2(){
        List<ServiceInstance> list = discoveryClient.getInstances("simpleConsumer");
        if(CollectionUtils.isNotEmpty(list)){
            ServiceInstance instance = list.get(0);
            EurekaDiscoveryClient.EurekaServiceInstance eurekaServiceInstance = (EurekaDiscoveryClient.EurekaServiceInstance) instance;
            String ip = eurekaServiceInstance.getInstanceInfo().getIPAddr();
            Integer port = instance.getPort();
            logger.info("tx-manager地址:{}",ip + ":" + port);
        }
        simpleClient.register2();
    }


}
