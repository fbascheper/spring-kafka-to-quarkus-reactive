package com.github.fbascheper.messaging.traffic.producer;

import com.github.fbascheper.messaging.domain.VehicleRouteChangeAdvice;
import com.github.fbascheper.messaging.domain.VehicleRouteTrafficHotspots;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.stream.Collectors;

/**
 * Emitter of {@link VehicleRouteChangeAdvice}-events.
 *
 * @author Frederieke Scheper
 * @since 07-11-2021
 */
@Component
public class VehicleRouteChangeAdviceEmitter {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String vehicleRouteChangeAdviceTopicName;

    @Inject
    VehicleRouteChangeAdviceEmitter(
            KafkaTemplate<String, Object> kafkaTemplate
            , @Value("${traffic.kafka.vehicle-route-change-advice-topic}") String vehicleRouteChangeAdviceTopicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.vehicleRouteChangeAdviceTopicName = vehicleRouteChangeAdviceTopicName;
    }

    /**
     * Create the route change advice, if applicable,and send it to Kafka.
     *
     * @param vehicleRouteTrafficHotspots
     */
    public void sendRouteChangeAdvice(VehicleRouteTrafficHotspots vehicleRouteTrafficHotspots) {

        if (this.isRouteChangeAdvisable(vehicleRouteTrafficHotspots)) {

            var vehicleRouteChangeAdvice = routeChangeAdvice(vehicleRouteTrafficHotspots);

            kafkaTemplate.send(vehicleRouteChangeAdviceTopicName
                    , vehicleRouteChangeAdvice.vehicleId()
                    , vehicleRouteChangeAdvice
            );
        }
    }

    /**
     * Create the route change advice, based on the hotspots encountered.
     *
     * @param vehicleRouteTrafficHotspots the hotspots along the way
     * @return route change advice
     */
    private VehicleRouteChangeAdvice routeChangeAdvice(VehicleRouteTrafficHotspots vehicleRouteTrafficHotspots) {
        var message = "Suggest a new route instead of current route = " +
                vehicleRouteTrafficHotspots.trafficHotspotsOnRoute().stream()
                        .map(java.lang.Record::toString)
                        .collect(Collectors.joining(","));

        return new VehicleRouteChangeAdvice(vehicleRouteTrafficHotspots.vehicleId(), message);
    }

    private boolean isRouteChangeAdvisable(VehicleRouteTrafficHotspots vehicleRouteTrafficHotspots) {
        // Look for another route if there are too many hotspots on the current route
        return vehicleRouteTrafficHotspots.trafficHotspotsOnRoute().size() >= 1;
    }


}


