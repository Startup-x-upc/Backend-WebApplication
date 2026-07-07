package org.example.backendwebapplication.shared.domain.services;

/**
 * Interface representing the port for real-time notifications.
 */
public interface RealtimePublisher {
    /**
     * Publishes an event to a specific channel.
     *
     * @param channelName the name of the channel
     * @param eventName   the name of the event
     * @param payload     the event payload
     */
    void publish(String channelName, String eventName, Object payload);
}
