package com.avanzarit.platform.saas.aws.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;

/**
 * The JsonMessageFactory class is a custom {@link MessageFactory} implementation that maps a log message to a JSON
 * representation using the Jackson {@link ObjectMapper}.
 */
public class JsonMessageFactory implements MessageFactory {

    private ObjectMapper objectMapper;

    /**
     * Creates a new JsonMessageFactory.
     */
    public JsonMessageFactory() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Message newMessage(Object message) {
        try {
            return newMessage(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            return newMessage("Could not serialize log message");
        }
    }

    @Override
    public Message newMessage(String message) {
        return new SimpleMessage(message);
    }

    @Override
    public Message newMessage(String message, Object... params) {
        return newMessage(message);
    }
}
