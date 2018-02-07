package org.skyCloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

/**
 * 开启Zuul,服务网关  服务路由、均衡负载  权限控制
 */
@EnableZuulProxy
@SpringCloudApplication
public class ZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}

	/**
	 * 自定义服务与路由映射的生成关系 service-v1 生成 /v1/service/
	 * todo  待验证 2018年2月7日 16:16:05
	 */
	@Bean
	public PatternServiceRouteMapper serviceRouteMapper(){
		return new PatternServiceRouteMapper(
				"(?<name>^.+)-(?<version>v.+$)",//匹配服务名称是否匹配该正则
				"${version}/${name}"//根据匹配内容生成路径表达式
		);
	}

	/**动态路由*/
	@Bean
	@RefreshScope
	@ConfigurationProperties("zuul")
	public ZuulProperties zuulProperties(){
		return  new ZuulProperties();
	}
}
