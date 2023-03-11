package com.codependent.multijoin.configuration;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class KafkaConfiguration {

    @Bean
    public KafkaAdmin kafkaAdmin(@Value("${spring.kafka.bootstrap-servers}")String bootstrapAddress) {
        Map<String, Object> configs = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic merged() {
        return new NewTopic("merged", 1, (short) 1);
    }

    @Bean
    public NewTopic x() {
        return new NewTopic("x", 1, (short) 1);
    }

    @Bean
    public NewTopic y() {
        return new NewTopic("y", 1, (short) 1);
    }

    @Bean
    public NewTopic z() {
        return new NewTopic("z", 1, (short) 1);
    }

}
