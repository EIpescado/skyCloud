package org.skyCloud.client;

import org.skyCloud.common.dataWrapper.BackResult;
import org.skyCloud.dto.UserRegister;
import org.skyCloud.hystrix.SimpleClientFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by yq on 2017/07/04 17:04.
 * 服务调用
 * //name :远程服务名
 * @author yq
 */
@FeignClient(name= "system",fallback = SimpleClientFallback.class)
public interface SimpleClient {

    @GetMapping("/menu")
    BackResult list();

    @PostMapping(value = "/user/register")
    BackResult register(@RequestBody UserRegister userRegister);

    @PostMapping(value = "/user/register2")
    BackResult register2();

}
