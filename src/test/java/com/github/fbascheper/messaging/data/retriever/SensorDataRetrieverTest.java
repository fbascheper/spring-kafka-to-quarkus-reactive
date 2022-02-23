package com.github.fbascheper.messaging.data.retriever;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test class for {@link SensorDataRetriever}.
 *
 * @author Frederieke Scheper
 * @since 03-11-2021
 */
class SensorDataRetrieverTest {

    String baseUrl = "http://miv.opendata.belfla.be/miv/configuratie/xml";
    SensorDataRetriever instance = new SensorDataRetriever(baseUrl);

    @Test
    void getSensorData() {
        instance.onPostConstruct();
        var sensorData = instance.getTrafficSensors();

        assertThat(sensorData, notNullValue());
        assertThat(sensorData.isEmpty(), is(false));

    }
}
