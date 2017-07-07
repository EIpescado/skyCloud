package org.skyCloud.simpleConsumer.hystrix;

import feign.hystrix.FallbackFactory;
import org.skyCloud.simpleConsumer.client.SimpleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

/**
 * Created by yq on 2017/07/06 15:57.
 * 服务调用回调工厂
 */
@Component
public class SimpleClientFallbackFactory implements FallbackFactory<SimpleClient> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public SimpleClient create(Throwable throwable) {
        logger.error("服务:{} 不可用,原因: {}",discoveryClient.getLocalServiceInstance().getServiceId(),throwable.getMessage());
        return (a, b) -> null;
    }
}
