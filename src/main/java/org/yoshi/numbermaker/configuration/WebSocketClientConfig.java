package org.yoshi.numbermaker.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;

@Configuration
public class WebSocketClientConfig {

    @Bean
    public WebSocketSession webSocketSession() throws Exception {
        Logger logger = LoggerFactory.getLogger(WebSocketClientConfig.class.getName());
        WebSocketClient client = new StandardWebSocketClient();


        return client.doHandshake(new TextWebSocketHandler() {
            @Override
            public void handleTextMessage(WebSocketSession session, TextMessage message) {
                logger.info("Received response from server: {}", message.getPayload());
            }
        }, URI.create("ws://numbermaster:8081/ws/numbers").toString()).get();
    }
}

