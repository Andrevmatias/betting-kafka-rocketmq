package com.betting.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMqConfig {

	@Bean
	public RocketMQTemplate rocketMQTemplate(DefaultMQProducer rocketMQProducer) {
		RocketMQTemplate template = new RocketMQTemplate();
		rocketMQProducer.setAutoBatch(true);
		template.setProducer(rocketMQProducer);
		return template;
	}
}
