package com.github.fbascheper.messaging.traffic.consumer;

import com.github.fbascheper.messaging.domain.VehicleRouteChangeAdvice;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Consumer of the {@link VehicleRouteChangeAdvice}, sent to Kafka.
 * Note: this is used solely for demo purposes...
 *
 * @author Frederieke Scheper
 * @since 20-10-2021
 */
@Component
public class VehicleRouteChangeAdviceConsumer {

    private static final Logger LOGGER = getLogger(VehicleRouteChangeAdviceConsumer.class);

    /**
     * Handle the incoming {@link VehicleRouteChangeAdvice} and log it for demo purposes..
     */
    @KafkaListener(topics = "${traffic.kafka.vehicle-route-change-advice-topic}"
            , clientIdPrefix = "routeChangeAdviceJson"
            , groupId = "cgRouteAdvice"
            , containerFactory = "kafkaListenerContainerFactory")
    public void listenAsObject(
            ConsumerRecord<String, VehicleRouteChangeAdvice> consumerRecord
            , @Payload VehicleRouteChangeAdvice advice
    ) {
        LOGGER.debug("Received advice from Kafka => {}", advice);
    }


}
