package com.springcloud.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HelloService {
	@Autowired
	private RestTemplate restTemplate;

	public String hello(String name) {
		String forObject = restTemplate.getForObject("http://producer/producer/hello/" + name, String.class);
		log.info("ribbon producer----- response={}", forObject);
		return forObject;
	}
}
