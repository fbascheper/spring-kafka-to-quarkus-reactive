package com.github.fbascheper.messaging.domain;

import java.math.BigDecimal;

/**
 * Geographic coordinates.
 *
 * @param longitude Decimal longitude according to WGS84-projection (EPSG:4326)
 * @param latitude  Decimal latitude according to WGS84-projection (EPSG:4326)
 * @author Frederieke Scheper
 * @since 04-11-2021
 */
public record GeographicCoordinates(

        BigDecimal longitude
        , BigDecimal latitude

) {
}
