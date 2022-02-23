package com.github.fbascheper.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;

/**
 * meetup-gn-spring-pipeline - Description.
 *
 * @author Frederieke Scheper
 * @since 18-10-2021
 */
@Component
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    @Inject
    public KafkaConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        this.objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build().setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    }

    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactory() {
        var jsonSerializer = new JsonSerializer<>(objectMapper);
        return new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties(), new StringSerializer(), jsonSerializer);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        var jsonDeserializer = new JsonDeserializer<>(objectMapper);
        jsonDeserializer.addTrustedPackages("com.github.fbascheper.*");

        var objectConsumerFactory = new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(), new StringDeserializer(), jsonDeserializer);

        var result = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        result.setConsumerFactory(objectConsumerFactory);

        return result;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerStringContainerFactory() {
        var stringConsumerFactory = new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(), new StringDeserializer(), new StringDeserializer());

        var result = new ConcurrentKafkaListenerContainerFactory<String, String>();
        result.setConsumerFactory(stringConsumerFactory);

        return result;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
