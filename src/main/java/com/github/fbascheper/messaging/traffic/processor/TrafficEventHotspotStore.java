package com.github.fbascheper.messaging.traffic.processor;

import com.github.fbascheper.messaging.domain.TrafficEvent;
import com.github.fbascheper.messaging.domain.VehicleClass;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Store traffic event hotspots.
 *
 * @author Frederieke Scheper
 * @since 07-11-2021
 */
@ApplicationScoped
public class TrafficEventHotspotStore {

    private static final Logger LOGGER = getLogger(TrafficEventHotspotStore.class);

    private final ConcurrentMap<Integer, ConcurrentMap<VehicleClass, TrafficEvent>> trafficEventHotspots = new ConcurrentHashMap<>();

    @Incoming("traffic-event-hotspot")
    public void store(TrafficEvent trafficEvent) {

        LOGGER.trace("Storing hotspot event = {}", trafficEvent);

        trafficEventHotspots.compute(trafficEvent.sensorId(), (key, value) -> {
            value = (value == null ? new ConcurrentHashMap<>() : value);
            value.put(trafficEvent.vehicleClass(), trafficEvent);
            return value;
        });

    }

    public ConcurrentMap<VehicleClass, TrafficEvent> hotspotsOfSensorId(Integer sensorId) {
        var result = trafficEventHotspots.getOrDefault(sensorId, new ConcurrentHashMap<>());

        LOGGER.trace("Looking for hotspots for sensorId = {}, found = {}", sensorId, result.size());

        return result;
    }

}
