package com.github.fbascheper.messaging.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fbascheper.messaging.config.KafkaConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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

    ObjectMapper objectMapper = new KafkaConfiguration(null).getObjectMapper();


    @Test
    void serializeTrafficEvent() throws IOException {
        var serializedEvent = objectMapper.writeValueAsString(trafficEvent);

        assertThat(serializedEvent, is(serializedTrafficEvent));
    }

    @Test
    void deserializeTrafficEvent() throws IOException {
        TrafficEvent deserializedEvent = objectMapper.readValue(serializedTrafficEvent, TrafficEvent.class);

        assertThat(deserializedEvent, is(trafficEvent));
    }

}


