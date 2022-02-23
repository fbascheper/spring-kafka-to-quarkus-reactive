package com.github.fbascheper.messaging.traffic.producer;

import com.github.fbascheper.messaging.data.retriever.TrafficDataRetriever;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Emitter of traffic events, using a once-per-minute HTTP call to the traffic events endpoint published
 * by the Flemish road authorities.
 *
 * @author Frederieke Scheper
 * @since 20-10-2021
 */
@Component
@EnableScheduling
public class TrafficEventEmitter {

    private static final Logger LOGGER = getLogger(TrafficEventEmitter.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String trafficEventsTopicName;
    private final TrafficDataRetriever trafficDataRetriever;

    @Inject
    TrafficEventEmitter(
            KafkaTemplate<String, Object> kafkaTemplate
            , @Value("${traffic.kafka.traffic-event-topic}") String trafficEventsTopicName
            , TrafficDataRetriever trafficDataRetriever
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.trafficEventsTopicName = trafficEventsTopicName;
        this.trafficDataRetriever = trafficDataRetriever;
    }

    // TODO: reset traffic event update frequency to one every minute
    // @Scheduled(fixedRate = 60_000L)
    @Scheduled(fixedRate = 300_000L)
    public void sendTrafficEvents() {
        var trafficEvents = this.trafficDataRetriever.getTrafficEvents();
        trafficEvents.forEach(event ->
                kafkaTemplate.send(trafficEventsTopicName
                        , event.sensorId().toString()
                        , event
                ));
    }

}
