package org.skyCloud.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yq
 * @date 2018/02/05 17:23
 * @description
 * @since V1.0.0
 */
@Configuration
public class FeignConfig {

    /**
     * feign日志级别 默认为NONE 不记录任何日志
     * @return
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
