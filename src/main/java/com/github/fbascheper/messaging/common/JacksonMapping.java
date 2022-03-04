package com.github.fbascheper.messaging.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.smallrye.reactive.messaging.json.JsonMapping;
import org.slf4j.Logger;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import java.text.SimpleDateFormat;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation of the SmallRye {@link JsonMapping}-interface for Jackson.
 *
 * @author Frederieke Scheper
 * @since 20-10-2021
 */
@ApplicationScoped
@Priority(500)
public class JacksonMapping implements JsonMapping {

    private static final Logger LOGGER = getLogger(JacksonMapping.class);

    private final ObjectMapper objectMapper;

    /**
     * Default constructor.
     */
    public JacksonMapping() {
        this.objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build().setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @Override
    public String toJson(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException jpex) {
            throw new RuntimeException(jpex);
        }
    }

    @Override
    public <T> T fromJson(String str, Class<T> type) {
        try {
            return this.objectMapper.readValue(str, type);
        } catch (JsonProcessingException jpex) {

            throw new RuntimeException(jpex);
        }
    }
}
