package com.github.fbascheper.messaging.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Encapsulation of a "vehicle route change" event, which may signify any of the following:
 * <ul>
 *     <li>A new route to a given destination has been selected by the driver</li>
 *     <li>A vehicle has progressed along its route (geo-coords list has been changed)</li>
 *     <li>A vehicle has reached its destination (geo-coords list now only contains the current location)</li>
 * </ul>
 *
 *
 * @param vehicleId    the {@code id} of this vehicle
 * @param vehicleClass the {@link VehicleClass} of this vehicle
 * @param route        a list of {@link GeographicCoordinates}, containing at least one element: its current location;
 *                     and all subsequent elements make up the complete route to the vehicle's destination, like so:
 *                     <ul>
 *                     <li>the current location (at element 0)</li>
 *                     <li>Geo-coordinates of the route including the destination</li>
 *                     </ul>
 * @author Frederieke Scheper
 * @since 06-11-2021
 */
public record VehicleRouteChangeEvent(

        @JsonProperty("vehicleId") String vehicleId
        , @JsonProperty("vehicleClass") VehicleClass vehicleClass
        , @JsonProperty("route") List<GeographicCoordinates> route

) {

    /**
     * @return the current location of this vehicle
     */
    public GeographicCoordinates currentLocation() {
        return route.get(0);
    }

}
