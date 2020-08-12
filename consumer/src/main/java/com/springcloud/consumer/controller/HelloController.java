package com.springcloud.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springcloud.consumer.remote.HelloRemote;
import com.springcloud.consumer.service.HelloService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HelloController {

	@Autowired
	private HelloRemote helloRemote;
	@Autowired
	private HelloService helloService;

	@GetMapping("/feign/hello/{name}")
	public String feign(@PathVariable("name") String name) {
		log.info("consumer controller feign------name={}", name);
		return helloRemote.hello(name);
	}

	@GetMapping("/ribbon/hello/{name}")
	public String ribbon(@PathVariable("name") String name) {
		log.info("consumer controller ribbon-----name={}", name);
		return helloService.hello(name);
	}

}
