package com.github.fbascheper.messaging.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

/**
 * Information about a traffic event, as modeled by the Flemish road authorities.
 *
 * @param timeRegistration           Starting date and time of the minute to which the data correspond
 * @param vehicleClass               class of vehicle passing measurement point
 * @param vehicleCount               the number of vehicles counted in this vehicle class
 * @param vehicleAverageSpeed        Arithmetic average speed of the vehicles in this vehicle class
 *                                   <ul>
 *                                   <li>Calculated by {@code Sum (vi) / n}, with vi = individual speed of a vehicle in this vehicle class</li>
 *                                   <li>Value domain 0 to 254 km/h.</li>
 *                                   <li>Value range 0..200 km/h</li>
 *                                   <li> Special values:</li>
 *                                   <li>251: Initial value</li>
 *                                   <li>254: Calculation not possible</li>
 *                                   <li>252: no vehicles were counted in this vehicle class.</li>
 *                                   </ul>
 * @param vehicleHarmonicSpeed       Harmonic average speed of the vehicles in this vehicle class
 *                                   <ul>
 *                                   <li>Calculated by {@code n / Sum (1/vi)}, with vi = individual speed of a vehicle in this vehicle class</li>
 *                                   <li>Value domain 0 to 254 km/h.</li>
 *                                   <li>Value range 0..200 km/h</li>
 *                                   <li> Special values:</li>
 *                                   <li>251: Initial value</li>
 *                                   <li>254: Calculation not possible</li>
 *                                   <li>252: no vehicles were counted in this vehicle class.</li>
 *                                   </ul>
 * @param sensorId                   ID of the sensor
 * @param sensorDescriptiveId        Descriptive id of the sensor
 * @param sensorAvailable            {@code boolean} indicator if the sensor is available
 * @param sensorDataRecent           {@code boolean} indicator if the sensor's data is recent, where {@code false} may indicate a connection problem
 * @param sensorLastTimeOfDataUpdate timestamp of last update of data for this measurement point
 * @author Frederieke Scheper
 * @since 13-10-2021
 */
public record TrafficEvent(

        @JsonProperty("timeRegistration") ZonedDateTime timeRegistration
        , @JsonProperty("sensorId") Integer sensorId
        , @JsonProperty("sensorDescriptiveId") String sensorDescriptiveId
        , @JsonProperty("sensorAvailable") boolean sensorAvailable
        , @JsonProperty("sensorDataRecent") boolean sensorDataRecent
        , @JsonProperty("sensorLastTimeOfDataUpdate") ZonedDateTime sensorLastTimeOfDataUpdate
        , @JsonProperty("vehicleClass") VehicleClass vehicleClass
        , @JsonProperty("vehicleCount") int vehicleCount
        , @JsonProperty("vehicleAverageSpeed") int vehicleAverageSpeed
        , @JsonProperty("vehicleHarmonicSpeed") int vehicleHarmonicSpeed
) {
    public boolean vehiclesCountedInVehicleClass() {
        return vehicleCount > 0 && vehicleAverageSpeed != 252 && vehicleHarmonicSpeed != 252;
    }

    public boolean speedMeasurementOutsideRange() {
        return vehicleAverageSpeed > 200 || vehicleHarmonicSpeed > 200;
    }

}
