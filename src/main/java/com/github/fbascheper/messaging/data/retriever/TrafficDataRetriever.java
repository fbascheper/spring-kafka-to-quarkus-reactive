package com.github.fbascheper.messaging.data.retriever;

import com.github.fbascheper.messaging.common.TrafficDataConverter;
import com.github.fbascheper.messaging.domain.TrafficEvent;
import com.github.fbascheper.miv.data.JaxbMivType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Retriever of traffic events.
 *
 * @author Frederieke Scheper
 * @since 15-10-2021
 */
@Component
public class TrafficDataRetriever {

    private static final Logger LOGGER = getLogger(TrafficDataRetriever.class);

    private final String flemishTrafficDataUrl;
    private final HttpClient httpClient;
    private final JAXBContext jaxbContext;

    public TrafficDataRetriever(@Value("${traffic.flemish.data-url}") String trafficDataUrl) {
        Objects.requireNonNull(trafficDataUrl);

        this.flemishTrafficDataUrl = trafficDataUrl;

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        try {
            this.jaxbContext = JAXBContext.newInstance(JaxbMivType.class.getPackageName());
        } catch (JAXBException jex) {
            throw new IllegalStateException("Could not create JAXB context", jex);
        }
    }

    public List<TrafficEvent> getTrafficEvents() {
        var flemishTrafficData = getTrafficData();

        var result= flemishTrafficData.getMeetpunt().stream()
                .flatMap(meetpunt -> meetpunt.getMeetdata().stream()
                        .map(meetdata -> TrafficDataConverter.ofMeasurement(meetpunt, meetdata)))
                .toList();

        LOGGER.debug("Retrieved a total of {} traffic events", result.size());

        return result;
    }


    private JaxbMivType getTrafficData() {
        JaxbMivType result;

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(flemishTrafficDataUrl))
                .setHeader("User-Agent", "Java 17 HttpClient") // add request header
                .build();

        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            result =  toMivType(response.body());

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Could not retrieve Flemish traffic data", e);
        }

        LOGGER.debug("Retrieved data about of {} measurement points", result.getMeetpunt().size());
        return result;
    }

    private JaxbMivType toMivType(String xml) {
        JaxbMivType result;

        try {
            var um = jaxbContext.createUnmarshaller();

            @SuppressWarnings("unchecked")
            var element  = (JAXBElement<JaxbMivType>) um.unmarshal(new ByteArrayInputStream(xml.getBytes()));

            result = element.getValue();

        } catch (JAXBException je) {
            throw new IllegalStateException("An error occurred while retrieving the traffic data", je);
        }

        return result;
    }

}
