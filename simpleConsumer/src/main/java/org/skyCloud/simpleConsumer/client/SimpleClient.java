package org.skyCloud.simpleConsumer.client;

import org.skyCloud.simpleConsumer.hystrix.SimpleClientFallbackFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by yq on 2017/07/04 17:04.
 * 服务调用
 */
@FeignClient(name= "simpleService",fallbackFactory = SimpleClientFallbackFactory.class)//name :远程服务名
public interface SimpleClient {

    @RequestMapping(value = "/add")
    Integer add(@RequestParam(value = "a") Integer a, @RequestParam(value = "b") Integer b);


}
