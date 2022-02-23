package com.github.fbascheper.messaging.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Advice sent to a driver to select another route, based on the hotspots.
 *
 * @param vehicleId             the {@code id} of the vehicle
 * @param routeChangeSuggestion suggestion of an alternative route, based on traffic hotspots along its route
 * @author Frederieke Scheper
 * @since 06-11-2021
 */
public record VehicleRouteChangeAdvice(

        @JsonProperty("vehicleId") String vehicleId
        , @JsonProperty("suggestion") String routeChangeSuggestion

) {
}
