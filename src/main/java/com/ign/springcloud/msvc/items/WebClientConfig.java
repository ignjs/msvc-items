package com.ign.springcloud.msvc.items;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Value("${config.base.enbdpoint.msvc-products}")
	private String url;

	@Bean
	WebClient webClient(WebClient.Builder wcBuilder, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
		return wcBuilder.baseUrl(url).filter(lbFunction).build();
	}

	/* @Bean
	@LoadBalanced // Load balanced annotation is used to make the WebClient load balanced
	WebClient.Builder webClient() {
		return WebClient.builder().baseUrl(url);
	}*/
} 
