package com.github.fbascheper.messaging.traffic.processor;

import com.github.fbascheper.messaging.domain.TrafficEvent;
import com.github.fbascheper.messaging.traffic.component.TrafficEventHotspotStore;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Filter out any traffic events that are not of interest to our processing steps.
 *
 * @author Frederieke Scheper
 * @since 06-11-2021
 */
@Component
public class TrafficEventHotspotFilter {

    private final TrafficEventHotspotStore trafficEventHotspotStore;

    @Inject
    public TrafficEventHotspotFilter(TrafficEventHotspotStore trafficEventHotspotStore) {
        this.trafficEventHotspotStore = trafficEventHotspotStore;
    }

    @KafkaListener(topics = "${traffic.kafka.traffic-event-topic}"
            , clientIdPrefix = "trafficEventJson"
            , groupId = "cgRouteAdvice"
            , containerFactory = "kafkaListenerContainerFactory")
    public void listenAsObject(
            ConsumerRecord<String, TrafficEvent> consumerRecord
            , @Payload TrafficEvent event
    ) {

        if (event.sensorAvailable() // keep only the events from available sensors

                // keep only the events from reliable vehicle classes (i.e. discard motorcycles)
                && event.vehicleClass().isReliable()

                // keep only the events with traffic
                && event.vehicleCount() != 0
                && event.vehiclesCountedInVehicleClass()

                // keep only the events with speed measurements within defined range
                && !event.speedMeasurementOutsideRange()

                // keep only the events with a low harmonic speed (congestions)
                && event.vehicleHarmonicSpeed() < 50
        ) {
            trafficEventHotspotStore.store(event);
        }
    }

}
