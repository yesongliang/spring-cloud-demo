//package com.springcloud.consumer.config;
//
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.boot.autoconfigure.AutoConfigureBefore;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.cloud.openfeign.FeignAutoConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import feign.Feign;
//import okhttp3.ConnectionPool;
//
////不需要额外编写FeignOkHttpConfig，feign本身已经存在OkHttpFeignConfiguration了，不需要额外配置。
//@Configuration
//@ConditionalOnClass(Feign.class)
//@AutoConfigureBefore(FeignAutoConfiguration.class)
//public class FeignOkHttpConfig {
//
//	@Bean
//	public okhttp3.OkHttpClient okHttpClient() {
//		return new okhttp3.OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS).writeTimeout(120, TimeUnit.SECONDS).connectionPool(new ConnectionPool())
//				// .addInterceptor();
//				.build();
//	}
//}
