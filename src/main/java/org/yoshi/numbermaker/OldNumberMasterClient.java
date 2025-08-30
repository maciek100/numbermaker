package org.yoshi.numbermaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;

@Component
public class OldNumberMasterClient {
    private Logger logger = LoggerFactory.getLogger(OldNumberMasterClient.class.getName());
    private WebSocketSession session;

    @PostConstruct
    public void connect() {
        StandardWebSocketClient client = new StandardWebSocketClient();

        try {
            // handshake returns a Future<WebSocketSession>
            this.session = client.doHandshake(new TextWebSocketHandler() {
                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                    logger.info("Server says: {}", message.getPayload());
                }
            }, "ws://numbermaster:8081/ws/evaluate").get(); // block until connected

            // Send initial test
            this.session.sendMessage(new TextMessage("512"));

        } catch (InterruptedException | ExecutionException | java.io.IOException e) {
            logger.error("WS Connection error {}", e.getMessage());
        }
    }

    public void sendNumber(long number) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(String.valueOf(number)));
            }
        } catch (Exception e) {
            logger.error("WS Connection error while sending the number {} :  {}", number, e.getMessage());
        }
    }
}
