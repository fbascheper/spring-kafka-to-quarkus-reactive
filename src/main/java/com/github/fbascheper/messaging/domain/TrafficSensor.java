package com.github.fbascheper.messaging.domain;

/**
 * Information about a traffic sensor, as modeled by the Flemish road authorities.
 *
 * @param id                    Unique identification number of the measurement point.
 * @param descriptiveId         Descriptive id. (Internally used id. May be omitted in the future.)
 * @param name                  Rough textual description of the location. (An internally used description. May be omitted in the future.)
 * @param ident8                Unique road number.
 *                              <p>
 *                              More info in the dataset of numbered roads in the "Wegenregister" (Roads registry), field: locatieide,
 *                              https://data.gov.be/nl/dataset/7bc9a7ed-75fb-41a9-9cf7-06287989ca8f
 *                              </p>
 * @param trafficLane           Reference to the lane of the measurement point.
 *                              The character indicates the lane type.
 *                              <ul>
 *                              <li>R: Regular lane</li>
 *                              <li>B: Bus lane or similar</li>
 *                              <li>TR: measurement of the traffic in the opposite direction (p.e. in or near tunnels) on the corresponding R-lane.</li>
 *                              <li>P: Hard shoulder lane</li>
 *                              <li>W: parking or other road</li>
 *                              <li>S: Lane for hard shoulder running</li>
 *                              <li> A: Hatched area</li>
 *                              </ul>
 *                              <p>
 *                              Counting starts at R10 for the first regular lane of the main road. Lane numbers increase from right/slower to left/faster lanes.
 *                              Lanes 09, 08, 07, ... are positioned right of this first lane, and mainly indicate access/merging lanes, deceleration lanes, recently added lanes, lanes for hard shoulder running, bus lanes
 *                              Lanes 11, 12, 13, ... are positioned left of lane R10.
 *                              The lane number 00 is used for measurement points on the hard shoulder (P00).
 *                              The TR-lane is identical to the corresponding R-lane (TR10=R10,TR11=R11,TR12=R12,...), but returns the data of the "ghost traffic" instead.
 *                              (The data for TR10 and R10 are provided by the same detection loops.)
 *                              </p>
 * @param geographicCoordinates Geographic coordinates (latitude / longitude) according to WGS84-projection (EPSG:4326)
 * @author Frederieke Scheper
 * @since 13-10-2021
 */

public record TrafficSensor(

        Integer id
        , String descriptiveId
        , String name
        , String ident8
        , String trafficLane
        , GeographicCoordinates geographicCoordinates

) {
}

