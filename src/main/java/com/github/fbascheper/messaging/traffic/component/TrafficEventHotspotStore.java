package com.github.fbascheper.messaging.traffic.component;

import com.github.fbascheper.messaging.domain.TrafficEvent;
import com.github.fbascheper.messaging.domain.VehicleClass;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Store traffic event hotspots.
 *
 * @author Frederieke Scheper
 * @since 07-11-2021
 */
@Component
public class TrafficEventHotspotStore {

    private static final Logger LOGGER = getLogger(TrafficEventHotspotStore.class);

    private final ConcurrentMap<Integer, ConcurrentMap<VehicleClass, TrafficEvent>> trafficEventHotspots = new ConcurrentHashMap<>();

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
