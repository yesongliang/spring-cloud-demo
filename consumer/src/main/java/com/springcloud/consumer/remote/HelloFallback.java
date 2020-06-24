package com.springcloud.consumer.remote;

import org.springframework.stereotype.Component;

@Component
public class HelloFallback implements HelloRemote {

	@Override
	public String hello(String name) {
		return "服务暂不可用...";
	}

}
