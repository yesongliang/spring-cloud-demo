package com.springcloud.consumer.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "spring-cloud-producer", fallback = HelloFallback.class)
public interface HelloRemote {

	@GetMapping("/hello/{name}")
	public String hello(@PathVariable("name") String name);
}
