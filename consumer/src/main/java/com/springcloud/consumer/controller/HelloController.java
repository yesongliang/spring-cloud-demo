package com.springcloud.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springcloud.consumer.remote.HelloRemote;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HelloController {

	@Autowired
	private HelloRemote helloRemote;
//	@Autowired
//	private HelloService helloService;

	@GetMapping("/hello/{name}")
	public String index(@PathVariable("name") String name) {
		log.info("consumer controller-----name={}", name);
		return helloRemote.hello(name);
	}
}
