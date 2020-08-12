package com.springcloud.consumer.remote;

import org.springframework.stereotype.Component;

/**
 * hystrix
 * 
 * @author ysl
 *
 */
@Component
public class HelloFallback implements HelloRemote {

	@Override
	public String hello(String name) {
		return "服务暂不可用...";
	}

}
