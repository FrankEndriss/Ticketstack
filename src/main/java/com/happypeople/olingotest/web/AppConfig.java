package com.happypeople.olingotest.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Simple spring configuration class
 */
@Configuration
public class AppConfig {
	private Logger log=LoggerFactory.getLogger(AppConfig.class);

	@Bean
	public String myTestBean() {
		return "this is a simplest bean for testing";
	}

}
