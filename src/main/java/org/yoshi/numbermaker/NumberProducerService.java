package org.yoshi.numbermaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import org.yoshi.dto.NumberCheckRequest;
//import org.yoshi.dto.PrimeCheckResponse;

import java.util.concurrent.atomic.AtomicLong;

//@Service
public class NumberProducerService {

    private static final Logger logger = LoggerFactory.getLogger(NumberProducerService.class);

    private final WebClient webClient;
    private final AtomicLong counter = new AtomicLong(0);
    private RandomNumberMaker randomNumberMaker;
    @Value("${numbermaster.url:http://numbermaster:8081}")
    private String numberMasterUrl;

    public NumberProducerService() {
        this.randomNumberMaker = new RandomNumberMaker();
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    @Scheduled(fixedRate = 200)
    public void produceAndSendNumber() {
        long number = randomNumberMaker.produceNextNumber();

        logger.info("Producing number: {}", number);

        sendNumberToMaster(number);
    }

    private void sendNumberToMaster(long number) {
        NumberCheckRequest request = new NumberCheckRequest(number, counter.incrementAndGet());

        webClient.post()
                .uri(numberMasterUrl + "/api/numbers/evaluate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NumberCheckResponse2.class)
                .doOnSuccess(response -> handleResponse(response))
                .doOnError(error -> logger.error("Error sending number to NumberMaster: {}", error.getMessage()))
                .subscribe();
    }

    private void handleResponse(NumberCheckResponse2 response) {
        if (response != null) {
            logger.info("received response : {}", response);
        }
    }
}
