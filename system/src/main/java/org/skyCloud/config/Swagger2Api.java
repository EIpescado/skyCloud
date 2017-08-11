package org.skyCloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by yq on 2017/05/03 17:10.
 * restAPI 地址 /swagger-ui.html
 */
@Configuration
@EnableSwagger2
public class Swagger2Api {

    @Bean
    public Docket restApi(){
        return new Docket(DocumentationType.SWAGGER_12)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.skyCloud.web"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("系统API")
                .description("system API:调用地址/api/system/,系统相关服务")
                .termsOfServiceUrl("")
                .contact(new Contact("余谦","","1724537432@qq.com"))
                .version("v1.0")
                .build();
    }

}
