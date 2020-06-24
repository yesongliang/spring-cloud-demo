package com.springcloud.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//启用服务注册与发现
@EnableEurekaClient
//启用feign进行远程调用
@EnableFeignClients
public class Consumer {
	public static void main(String[] args) {
		SpringApplication.run(Consumer.class, args);
	}

//	@Bean
//	@LoadBalanced
//	RestTemplate restTemplate() {
//		return new RestTemplate();
//	}
}
