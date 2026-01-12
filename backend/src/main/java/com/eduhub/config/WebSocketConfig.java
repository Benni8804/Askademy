package com.eduhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time updates.
 * This enables STOMP over WebSocket for bidirectional communication between client and server.
 * 
 * Configuration details:
 * - STOMP endpoint: /ws (with SockJS fallback)
 * - Application destination prefix: /app
 * - Message broker: /topic for broadcasting to multiple subscribers
 * - Allowed origins: http://localhost:3000 (React frontend)
 * 
 * Usage in frontend (React):
 * 1. Install: npm install sockjs-client stompjs
 * 2. Connect:
 *    const socket = new SockJS('http://localhost:8080/ws');
 *    const stompClient = Stomp.over(socket);
 *    stompClient.connect({}, () => {
 *        stompClient.subscribe('/topic/questions', (message) => {
 *            const question = JSON.parse(message.body);
 *            // Handle new question
 *        });
 *    });
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@NonNull MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker to carry messages back to the client
        // Messages with destination prefix /topic will be routed to the broker
        config.enableSimpleBroker("/topic");
        
        // Prefix for messages bound for @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
        // Register the /ws endpoint for WebSocket connections
        // withSockJS() enables SockJS fallback options for browsers that don't support WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000") // React frontend
                .withSockJS();
    }
}
