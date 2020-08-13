package com.springcloud.producer.contorller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
//允许动态刷新配置
//@RefreshScope
public class HelloController {

	@Value("${foo}")
	private String value;

	@GetMapping("/hello/{name}")
	public String index(@PathVariable String name) {
		log.info("producer controller-----name={}", name);
		return value + "!" + name;
	}
}
