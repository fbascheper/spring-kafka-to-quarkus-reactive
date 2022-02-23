package com.github.fbascheper.messaging.traffic.component;

import com.github.fbascheper.messaging.domain.*;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Processor of "vehicle route traffic sensors" events.
 *
 * @author Frederieke Scheper
 * @since 27-10-2021
 */
@Component
public class VehicleRouteTrafficSensorsProcessor {

    private static final Logger LOGGER = getLogger(VehicleRouteTrafficSensorsProcessor.class);

    private final TrafficEventHotspotStore hotspotStore;

    @Inject
    public VehicleRouteTrafficSensorsProcessor(TrafficEventHotspotStore hotspotStore) {
        this.hotspotStore = hotspotStore;
    }

    /**
     * Find the traffic hotspots along a vehicle's route.
     *
     * @param vehicleRouteTrafficSensors a vehicle's route, containing the traffic sensors underway
     * @return the traffic hotspots
     */
    public VehicleRouteTrafficHotspots vehicleRouteHotspots(VehicleRouteTrafficSensors vehicleRouteTrafficSensors) {
        var trafficHotspotsOnRoute = vehicleRouteTrafficSensors
                .sensorsOnRoute().stream()
                .map(this::hotspotEventOrUnknown)
                .filter(trafficSensor -> trafficSensor.vehicleClass() != VehicleClass.UNKNOWN)
                .toList();

        var result = new VehicleRouteTrafficHotspots(vehicleRouteTrafficSensors.vehicleId(), Collections.unmodifiableList(trafficHotspotsOnRoute));
        LOGGER.debug("Created traffic hotspots {}", result);

        return result;
    }

    private TrafficEvent hotspotEventOrUnknown(TrafficSensor sensor) {
        // This traffic sensor is a hotspot if its id can be found in the traffic event hotspots
        var hotspots = hotspotStore.hotspotsOfSensorId(sensor.id());

        // TODO: improve selection of the hotspot event (aggregate them ??)
        TrafficEvent result;

        if (hotspots.containsKey(VehicleClass.CAR)) {
            result = hotspots.get(VehicleClass.CAR);
        } else if (hotspots.containsKey(VehicleClass.MINIVAN)) {
            result = hotspots.get(VehicleClass.MINIVAN);
        } else if (hotspots.containsKey(VehicleClass.RIGID_LORRIES)) {
            result = hotspots.get(VehicleClass.RIGID_LORRIES);
        } else if (hotspots.containsKey(VehicleClass.TRUCK_OR_BUS)) {
            result = hotspots.get(VehicleClass.TRUCK_OR_BUS);
        } else {
            result = new TrafficEvent(null, sensor.id(), sensor.descriptiveId(), false, false, null
                    , VehicleClass.UNKNOWN, 0, 0, 0);
        }

        return result;
    }

}
