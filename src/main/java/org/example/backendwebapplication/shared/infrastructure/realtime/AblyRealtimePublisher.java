package org.example.backendwebapplication.shared.infrastructure.realtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ably.lib.rest.AblyRest;
import io.ably.lib.types.AblyException;
import org.example.backendwebapplication.shared.domain.services.RealtimePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AblyRealtimePublisher implements RealtimePublisher {
    private static final Logger log = LoggerFactory.getLogger(AblyRealtimePublisher.class);
    private final AblyRest ablyRest;
    private final ObjectMapper objectMapper;

    public AblyRealtimePublisher(AblyRest ablyRest, ObjectMapper objectMapper) {
        this.ablyRest = ablyRest;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(String channelName, String eventName, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            ablyRest.channels.get(channelName).publish(eventName, jsonPayload);
            log.info("Successfully published event '{}' to Ably channel '{}'", eventName, channelName);
        } catch (AblyException e) {
            log.error("Failed to publish event '{}' to Ably channel '{}': {}", eventName, channelName, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to serialize event payload to JSON for channel '{}': {}", channelName, e.getMessage());
        }
    }
}
