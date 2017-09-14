package org.skyCloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin.server.EnableZipkinServer;

@SpringBootApplication
@EnableZipkinServer //启动ZipkinServer
public class ZipkinServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZipkinServerApplication.class, args);
	}
}
