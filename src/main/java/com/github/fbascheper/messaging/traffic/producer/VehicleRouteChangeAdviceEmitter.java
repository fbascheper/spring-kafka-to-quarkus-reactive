package com.github.fbascheper.messaging.traffic.producer;

import com.github.fbascheper.messaging.common.JacksonMapping;
import com.github.fbascheper.messaging.domain.VehicleRouteChangeAdvice;
import com.github.fbascheper.messaging.domain.VehicleRouteTrafficHotspots;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.stream.Collectors;

/**
 * Emitter of {@link VehicleRouteChangeAdvice}-events.
 *
 * @author Frederieke Scheper
 * @since 07-11-2021
 */
@ApplicationScoped
public class VehicleRouteChangeAdviceEmitter {

    private final JacksonMapping jacksonMapping;

    @Inject
    VehicleRouteChangeAdviceEmitter(JacksonMapping jacksonMapping) {
        this.jacksonMapping = jacksonMapping;
    }

    /**
     * Create the route change advice, if applicable,and send it to Kafka.
     *
     * @param vehicleRouteTrafficHotspots the traffic hotspots along the route
     * @return the route change advice produced to the Kafka topic
     */
    @Incoming("vehicle-route-traffic-hotspots")
    @Outgoing("vehicle-route-change-advice-kafka-pdr")
    public Multi<Record<String, String>> sendRouteChangeAdvice(Multi<VehicleRouteTrafficHotspots> vehicleRouteTrafficHotspots) {

        return vehicleRouteTrafficHotspots
                .filter(this::isRouteChangeAdvisable)
                .map(this::routeChangeAdvice)
                .map(advice -> Record.of(advice.vehicleId(), jacksonMapping.toJson(advice)))
                ;

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


