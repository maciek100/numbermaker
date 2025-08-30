package org.yoshi.numbermaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NumberProducer {
    private final Logger logger = LoggerFactory.getLogger(NumberProducer.class.getName());
    private final WebSocketSession session;
    private final AtomicInteger requestId = new AtomicInteger(0);
    private final RandomNumberMaker randomNumberMaker = new RandomNumberMaker();

    public NumberProducer(WebSocketSession session) {
        this.session = session;
    }

    @Scheduled(fixedRate = 1000) // every 1s
    public void sendNumber() throws IOException {
        if (session.isOpen()) {
            long number = randomNumberMaker.produceNextNumber();
            long id = requestId.incrementAndGet();
            String payload = String.format("{\"number\":%d,\"requestId\":%d}", number, id);

            session.sendMessage(new TextMessage(payload));
            logger.info("Sent: {}", payload);
        } else {
            logger.error("WebSocket session is closed, skipping send.");
        }
    }
}

