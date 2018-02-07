package org.skyCloud.config;

import feign.Feign;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author yq
 * @date 2018/02/06 15:18
 * @description 禁用Hystrix,单个client使用引用, 定义了必须使用 否则会出现多个
 * @since V1.0.0
@Configuration
 */
public class FeignDisableHystrixConfig {

    @Bean
    @Scope("prototype")
    @Qualifier("feignBuilder")
    public Feign.Builder feignBuilder(){
        return  Feign.builder();
    }

}
