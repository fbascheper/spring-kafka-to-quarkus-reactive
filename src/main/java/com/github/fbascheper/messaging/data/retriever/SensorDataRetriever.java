package com.github.fbascheper.messaging.data.retriever;

import com.github.fbascheper.messaging.common.SensorDataConverter;
import com.github.fbascheper.messaging.domain.TrafficSensor;
import com.github.fbascheper.miv.config.JaxbTMivconfig;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Retriever of sensor configuration data.
 *
 * @author Frederieke Scheper
 * @since 03-11-2021
 */
@Component
public class SensorDataRetriever {

    private static final Logger LOGGER = getLogger(SensorDataRetriever.class);

    private final String sensorConfigDataUrl;
    private final HttpClient httpClient;
    private final JAXBContext jaxbContext;

    private List<TrafficSensor> trafficSensors;

    public SensorDataRetriever(@Value("${traffic.flemish.sensor-config-url}") String sensorConfigDataUrl) {
        Objects.requireNonNull(sensorConfigDataUrl);
        this.sensorConfigDataUrl = sensorConfigDataUrl;

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        try {
            this.jaxbContext = JAXBContext.newInstance(JaxbTMivconfig.class.getPackageName());
        } catch (JAXBException jex) {
            throw new IllegalStateException("Could not create JAXB context", jex);
        }
    }

    @PostConstruct
    void onPostConstruct() {
        this.buildTrafficSensorList();
    }

    /**
     * Return the list of all {@link TrafficSensor}-instances installed by the Flemish road authorities.
     *
     * @return list of sensors.
     */
    public List<TrafficSensor> getTrafficSensors() {
        Objects.requireNonNull(trafficSensors, "Traffic sensor list should be initialized");
        return Collections.unmodifiableList(trafficSensors);
    }

    private void buildTrafficSensorList() {
        var sensorConfig = getSensorConfig();

        this.trafficSensors = sensorConfig.getMeetpunt().stream()
                .map(SensorDataConverter::ofMeasurementPoint)
                .toList();


        ZonedDateTime lastConfigChange = sensorConfig.getTijdLaatsteConfigWijziging().toGregorianCalendar().toZonedDateTime();
        LOGGER.debug("Retrieved a total of {} sensors, last changed at {}", this.trafficSensors.size(), lastConfigChange);
    }

    private JaxbTMivconfig getSensorConfig() {
        JaxbTMivconfig result;

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(sensorConfigDataUrl))
                .setHeader("User-Agent", "Java 17 HttpClient") // add request header
                .build();

        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            result = toMivConfig(response.body());

        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Could not retrieve sensor data", e);
        }

        return result;
    }

    private JaxbTMivconfig toMivConfig(String xml) {
        JaxbTMivconfig result;

        try {
            var um = jaxbContext.createUnmarshaller();

            @SuppressWarnings("unchecked")
            var element = (JAXBElement<JaxbTMivconfig>) um.unmarshal(new ByteArrayInputStream(xml.getBytes()));

            result = element.getValue();

        } catch (JAXBException je) {
            throw new IllegalStateException("An error occurred while retrieving the sensor data", je);
        }

        return result;
    }

}
