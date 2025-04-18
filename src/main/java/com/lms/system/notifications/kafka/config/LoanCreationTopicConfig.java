package com.lms.system.notifications.kafka.config;

import com.lms.system.loans.dto.LoanRequestDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


public class LoanCreationTopicConfig {


    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroup;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final Map<String, Object> properties = new HashMap<>(5);

    @PostConstruct
    public void init() {
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    @Bean
    public NewTopic userTopic() {
        return TopicBuilder.name("loan_creation")
                .partitions(5)
                .build();
    }

    public ConsumerFactory<String, LoanRequestDTO> userConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                properties,
                new StringDeserializer(),
                new JsonDeserializer<>(LoanRequestDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LoanRequestDTO> loanCreationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LoanRequestDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userConsumerFactory());
        return factory;
    }
}
