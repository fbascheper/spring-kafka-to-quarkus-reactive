package com.github.fbascheper.messaging.traffic.producer;

import com.github.fbascheper.messaging.common.JacksonMapping;
import com.github.fbascheper.messaging.data.retriever.TrafficDataRetriever;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Emitter of traffic events, using a once-per-minute HTTP call to the traffic events endpoint published
 * by the Flemish road authorities.
 *
 * @author Frederieke Scheper
 * @since 20-10-2021
 */
@ApplicationScoped
public class TrafficEventEmitter {

    private static final Logger LOGGER = getLogger(TrafficEventEmitter.class);

    private final TrafficDataRetriever trafficDataRetriever;
    private final JacksonMapping jacksonMapping;

    @Inject
    TrafficEventEmitter(
            TrafficDataRetriever trafficDataRetriever
            , JacksonMapping jacksonMapping
    ) {
        this.trafficDataRetriever = trafficDataRetriever;
        this.jacksonMapping = jacksonMapping;
    }

    @Outgoing("traffic-event-kafka-pdr")
    public Multi<Record<String, String>> sendTrafficEvents() {

        // TODO: reset traffic event update frequency to one every minute
        // var ticks = Multi.createFrom().ticks().every(Duration.ofMinutes(1));

        var ticks = Multi.createFrom().ticks().every(Duration.ofMinutes(5));
        var events = ticks.onItem().transformToMultiAndConcatenate(tick ->
                Multi.createFrom().items(this.trafficDataRetriever.getTrafficEvents().stream()));
        return events.map(event -> Record.of(event.sensorId().toString(), jacksonMapping.toJson(event)));
    }

}
