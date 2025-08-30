package org.yoshi.numbermaker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import org.springframework.web.reactive.socket.client.StandardWebSocketClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.yoshi.dto.PrimeCheckRequest;

import java.net.URI;

public class NumberMasterClient {

    private final StandardWebSocketClient client;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(NumberMasterClient.class.getName());

    public NumberMasterClient () {
        this.client = new StandardWebSocketClient();
        this.objectMapper = new ObjectMapper();
    }

    public void connectAndSend () throws Exception {
        WebSocketSession session = client.doHandshake(new TextWebSocketHandler() {
                                                          public void handleTextMessage(WebSocketSession session, TextMessage message) {
                                                              logger.info("Received: {}", message.getPayload());
                                                          }
                                                      },
                new WebSocketHttpHeaders(),
                URI.create("ws://localhost:8081/ws/numbers")).get();

        for (int i = 0; i < 5; i++) {
            PrimeCheckRequest request = new PrimeCheckRequest(123L + i, 1L + i);
            String json = objectMapper.writeValueAsString(request);
            session.sendMessage(new TextMessage(json));
        }
        Thread.sleep(10000);
        logger.info("Done");
        session.close();
    }

    public static void main(String[] args) throws Exception{
        new NumberMasterClient().connectAndSend();
    }

}
