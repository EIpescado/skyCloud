package org.skyCloud.client;

import org.skyCloud.common.dataWrapper.BackResult;
import org.skyCloud.hystrix.SimpleClientFallbackFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by yq on 2017/07/04 17:04.
 * 服务调用
 * //name :远程服务名
 * @author yq
 */
@FeignClient(name= "system",fallbackFactory = SimpleClientFallbackFactory.class)
public interface SimpleClient {

    @GetMapping("/menu")
    BackResult list();


}
