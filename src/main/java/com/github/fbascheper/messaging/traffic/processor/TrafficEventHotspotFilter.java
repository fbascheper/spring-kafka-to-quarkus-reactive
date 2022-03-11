package com.github.fbascheper.messaging.traffic.processor;

import com.github.fbascheper.messaging.common.JacksonMapping;
import com.github.fbascheper.messaging.domain.TrafficEvent;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Filter out any traffic events that are not of interest to our processing steps.
 *
 * @author Frederieke Scheper
 * @since 06-11-2021
 */
@ApplicationScoped
public class TrafficEventHotspotFilter {

    private final JacksonMapping jacksonMapping;

    @Inject
    public TrafficEventHotspotFilter(JacksonMapping jacksonMapping) {
        this.jacksonMapping = jacksonMapping;
    }

    @Incoming("traffic-event-kafka-csr")
    @Outgoing("traffic-event-hotspot")
    public Multi<TrafficEvent> consume(Multi<String> trafficEventJson) {
        return trafficEventJson
                .map(str -> jacksonMapping.fromJson(str, TrafficEvent.class))

                // keep only the events from available sensors
                .filter(TrafficEvent::sensorAvailable)

                // keep only the events from reliable vehicle classes (i.e. discard motorcycles)
                .filter(event -> event.vehicleClass().isReliable())

                // keep only the events with traffic
                .filter(event -> event.vehicleCount() != 0
                        && event.vehiclesCountedInVehicleClass())

                // keep only the events with speed measurements within defined range
                .filter(event -> !event.speedMeasurementOutsideRange())

                // keep only the events with a low harmonic speed (congestions)
                .filter(event -> event.vehicleHarmonicSpeed() < 50)
                ;
    }

}
