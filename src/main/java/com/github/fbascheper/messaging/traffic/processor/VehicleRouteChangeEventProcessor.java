package com.github.fbascheper.messaging.traffic.processor;

import com.github.fbascheper.messaging.common.JacksonMapping;
import com.github.fbascheper.messaging.data.retriever.SensorDataRetriever;
import com.github.fbascheper.messaging.domain.GeographicCoordinates;
import com.github.fbascheper.messaging.domain.TrafficSensor;
import com.github.fbascheper.messaging.domain.VehicleRouteChangeEvent;
import com.github.fbascheper.messaging.domain.VehicleRouteTrafficSensors;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
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
@ApplicationScoped
public class VehicleRouteChangeEventProcessor {

    private static final Logger LOGGER = getLogger(VehicleRouteChangeEventProcessor.class);

    private final SensorDataRetriever sensorDataRetriever;
    private final JacksonMapping jacksonMapping;

    @Inject
    @Channel("vehicle-route-change-event-kafka-csr")
    Multi<String> vehicleRouteChangeEventJson;

    /**
     * All incoming "vehicle route change events" from Kafka, as JSON
     */
    @Inject
    VehicleRouteChangeEventProcessor(
            SensorDataRetriever sensorDataRetriever
            , JacksonMapping jacksonMapping
    ) {
        this.sensorDataRetriever = sensorDataRetriever;
        this.jacksonMapping = jacksonMapping;
    }

    /**
     * Handle the incoming {@link VehicleRouteChangeEvent}, potentially leading to "vehicle route change advice".
     */
    @Outgoing("vehicle-route-traffic-sensors")
    public Multi<VehicleRouteTrafficSensors> process() {
        return vehicleRouteChangeEventJson
                .map(str -> jacksonMapping.fromJson(str, VehicleRouteChangeEvent.class))
                .map(this::vehicleRouteTrafficSensors)
                ;
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
