package com.github.fbascheper.messaging.data.retriever;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test class for {@link TrafficDataRetriever}.
 *
 * @author Frederieke Scheper
 * @since 15-10-2021
 */
class TrafficDataRetrieverTest {

    String baseUrl = "http://miv.opendata.belfla.be/miv/verkeersdata";
    TrafficDataRetriever instance = new TrafficDataRetriever(baseUrl);

    @Test
    void getTrafficEvents() {
        var trafficEvents = instance.getTrafficEvents();

        assertThat(trafficEvents, notNullValue());
        assertThat(trafficEvents.isEmpty(), is(false));

    }
}
