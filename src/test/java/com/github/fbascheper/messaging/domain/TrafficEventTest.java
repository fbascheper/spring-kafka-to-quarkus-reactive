package com.github.fbascheper.messaging.domain;

import com.github.fbascheper.messaging.common.JacksonMapping;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test class for JSON serialization of {@link TrafficEvent}-instances.
 *
 * @author Frederieke Scheper
 * @since 15-10-2021
 */
class TrafficEventTest {
    ZoneId zoneId = ZoneId.of("UTC");

    ZonedDateTime timeRegistration = ZonedDateTime.of(2021, 9, 15, 23, 48, 20, 0, zoneId);
    ZonedDateTime lastUpdated = ZonedDateTime.of(2021, 10, 15, 23, 48, 20, 0, zoneId);

    TrafficEvent trafficEvent = new TrafficEvent(timeRegistration, 2500, "description", true, true,
            lastUpdated, VehicleClass.CAR, 25, 50, 52);

    String serializedTrafficEvent = "{\"timeRegistration\":\"2021-09-15T23:48:20Z\",\"sensorId\":2500,\"sensorDescriptiveId\":\"description\",\"sensorAvailable\":true,\"sensorDataRecent\":true,\"sensorLastTimeOfDataUpdate\":\"2021-10-15T23:48:20Z\",\"vehicleClass\":\"CAR\",\"vehicleCount\":25,\"vehicleAverageSpeed\":50,\"vehicleHarmonicSpeed\":52}";

    JacksonMapping jacksonMapping = new JacksonMapping();

    @Test
    void serializeTrafficEvent() {
        var serializedEvent = jacksonMapping.toJson(trafficEvent);

        assertThat(serializedEvent, is(serializedTrafficEvent));
    }

    @Test
    void deserializeTrafficEvent() {
        TrafficEvent deserializedEvent = jacksonMapping.fromJson(serializedTrafficEvent, TrafficEvent.class);

        assertThat(deserializedEvent, is(trafficEvent));
    }

}


