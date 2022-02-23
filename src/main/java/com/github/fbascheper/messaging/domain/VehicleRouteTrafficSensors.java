package com.github.fbascheper.messaging.domain;

import java.util.List;

/**
 * A record containing the traffic sensors on a vehicle's route.
 *
 * @param vehicleId      the {@code id} of this vehicle
 * @param sensorsOnRoute a list of {@link TrafficSensor}-instances on the vehicle's current route
 * @author Frederieke Scheper
 * @since 07-11-2021
 */
public record VehicleRouteTrafficSensors(

        String vehicleId
        , List<TrafficSensor> sensorsOnRoute

) {
}
