package org.skyCloud.hystrix;

import feign.hystrix.FallbackFactory;
import org.skyCloud.client.SimpleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 服务调用回调工厂 Hystrix的融断机制来避免在微服务架构中个别服务出现异常时引起的故障蔓延
 * @author yq on 2017/07/06 15:57.
 */
@Component
public class SimpleClientFallbackFactory implements FallbackFactory<SimpleClient> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public SimpleClient create(Throwable throwable) {
        logger.error("{} 服务不可用,原因: {}","系统",throwable);
        return () -> null;
    }
}
