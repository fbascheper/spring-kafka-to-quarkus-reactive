package com.github.fbascheper.messaging.traffic.processor;

import com.github.fbascheper.messaging.data.retriever.SensorDataRetriever;
import com.github.fbascheper.messaging.domain.GeographicCoordinates;
import com.github.fbascheper.messaging.domain.TrafficSensor;
import com.github.fbascheper.messaging.domain.VehicleRouteChangeEvent;
import com.github.fbascheper.messaging.domain.VehicleRouteTrafficSensors;
import com.github.fbascheper.messaging.traffic.component.VehicleRouteTrafficSensorsProcessor;
import com.github.fbascheper.messaging.traffic.producer.VehicleRouteChangeAdviceEmitter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Processor of "vehicle route change" events.
 *
 * @author Frederieke Scheper
 * @since 06-11-2021
 */
@Component
public class VehicleRouteChangeEventProcessor {

    private static final Logger LOGGER = getLogger(VehicleRouteChangeEventProcessor.class);

    private final SensorDataRetriever sensorDataRetriever;
    private final VehicleRouteTrafficSensorsProcessor vehicleRouteTrafficSensorsProcessor;
    private final VehicleRouteChangeAdviceEmitter vehicleRouteChangeAdviceEmitter;

    /**
     * All incoming "vehicle route change events" from Kafka, as JSON
     */
    @Inject
    VehicleRouteChangeEventProcessor(
            SensorDataRetriever sensorDataRetriever
            , VehicleRouteTrafficSensorsProcessor vehicleRouteTrafficSensorsProcessor
            , VehicleRouteChangeAdviceEmitter vehicleRouteChangeAdviceEmitter
    ) {
        this.sensorDataRetriever = sensorDataRetriever;
        this.vehicleRouteTrafficSensorsProcessor = vehicleRouteTrafficSensorsProcessor;
        this.vehicleRouteChangeAdviceEmitter = vehicleRouteChangeAdviceEmitter;
    }
    
    /**
     * Handle the incoming {@link VehicleRouteChangeEvent}, potentially leading to "vehicle route change advice".
     */
    @KafkaListener(topics = "${traffic.kafka.vehicle-route-change-event-topic}"
            , clientIdPrefix = "routeChangeEventJson"
            , groupId = "cgRouteAdvice"
            , containerFactory = "kafkaListenerContainerFactory")
    public void listenAsObject(
            ConsumerRecord<String, VehicleRouteChangeEvent> consumerRecord
            , @Payload VehicleRouteChangeEvent event
    ) {

        var vehicleRouteTrafficSensors = this.vehicleRouteTrafficSensors(event);
        var vehicleRouteHotspots = vehicleRouteTrafficSensorsProcessor.vehicleRouteHotspots(vehicleRouteTrafficSensors);
        vehicleRouteChangeAdviceEmitter.sendRouteChangeAdvice(vehicleRouteHotspots);
    }

    /**
     * Find all traffic sensors along the route of an incoming {@link VehicleRouteChangeEvent}.
     *
     * @param vehicleRouteChangeEvent incoming event
     * @return the outgoing event
     */
    private VehicleRouteTrafficSensors vehicleRouteTrafficSensors(VehicleRouteChangeEvent vehicleRouteChangeEvent) {
        // Note: this would be really geo-complex, but since we're actually using sensor geo-coords for the route,
        // this is now almost trivial.

        var trafficSensors = vehicleRouteChangeEvent.route().stream()
                .map(this::trafficSensor)
                .collect(Collectors.toList());

        var result = new VehicleRouteTrafficSensors(vehicleRouteChangeEvent.vehicleId(), Collections.unmodifiableList(trafficSensors));

        LOGGER.debug("Created result = {} from input = {}", result, vehicleRouteChangeEvent);

        return result;
    }

    private TrafficSensor trafficSensor(GeographicCoordinates coordinates) {
        return this.sensorDataRetriever.getTrafficSensors()
                .stream()
                .filter(trafficSensor -> trafficSensor.geographicCoordinates().equals(coordinates))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not find coords " + coordinates + " in traffic sensors"));
    }

}
