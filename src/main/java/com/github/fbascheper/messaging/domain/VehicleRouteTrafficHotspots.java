package com.github.fbascheper.messaging.domain;

import java.util.List;

/**
 * A record containing the traffic hotspots on a vehicle's route, that may lead to a {@link VehicleRouteChangeAdvice}.
 *
 * @param vehicleId              the {@code id} of this vehicle
 * @param trafficHotspotsOnRoute a list of {@link TrafficEvent}-hotspots on the vehicle's current route
 * @author Frederieke Scheper
 * @since 07-11-2021
 */
public record VehicleRouteTrafficHotspots(

        String vehicleId
        , List<TrafficEvent> trafficHotspotsOnRoute

) {
}
