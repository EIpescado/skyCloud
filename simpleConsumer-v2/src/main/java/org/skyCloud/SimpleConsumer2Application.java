package org.skyCloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient//启用服务注册与发现
@EnableFeignClients(basePackages = {"org.skyCloud.client"}) //启用feign进行远程调用
@ComponentScan("org.skyCloud")
public class SimpleConsumer2Application {

	public static void main(String[] args) {
		SpringApplication.run(SimpleConsumer2Application.class, args);
	}
}
