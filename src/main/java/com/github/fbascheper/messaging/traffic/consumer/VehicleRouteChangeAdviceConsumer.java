package com.github.fbascheper.messaging.traffic.consumer;

import com.github.fbascheper.messaging.common.JacksonMapping;
import com.github.fbascheper.messaging.domain.VehicleRouteChangeAdvice;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Consumer of the {@link VehicleRouteChangeAdvice}, sent to Kafka.
 * Note: this is used solely for demo purposes...
 *
 * @author Frederieke Scheper
 * @since 20-10-2021
 */
@ApplicationScoped
public class VehicleRouteChangeAdviceConsumer {

    private static final Logger LOGGER = getLogger(VehicleRouteChangeAdviceConsumer.class);

    private final JacksonMapping jacksonMapping;

    @Inject
    public VehicleRouteChangeAdviceConsumer(JacksonMapping jacksonMapping) {
        this.jacksonMapping = jacksonMapping;
    }

    /**
     * Handle the incoming {@link VehicleRouteChangeAdvice} and log it for demo purposes.
     */
    @Incoming("vehicle-route-change-advice-kafka-csr")
    public void consume(String adviceJson) {

        var advice = jacksonMapping.fromJson(adviceJson, VehicleRouteChangeAdvice.class);
        LOGGER.debug("Received advice from Kafka => {}", advice);
    }


}
