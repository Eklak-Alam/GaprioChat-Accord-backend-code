package com.gaprio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure STOMP endpoints (HTTP upgrade path).
     * Example client will connect to ws://<host>/ws
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // adjust for prod
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // optional SockJS fallback
    }

    /**
     * Configure the broker:
     * - /app prefix is where clients SEND to (mapped to @MessageMapping)
     * - /topic prefix is where we BROADCAST to subscribers
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic"); // or use RabbitMQ/ActiveMQ for scale
        // registry.setUserDestinationPrefix("/user"); // if using user queues
    }
}


