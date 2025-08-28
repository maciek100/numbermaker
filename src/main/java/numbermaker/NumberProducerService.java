package numbermaker;

import numbermaker.dto.NumberRequest;
import numbermaker.dto.NumberResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NumberProducerService {

    private static final Logger logger = LoggerFactory.getLogger(NumberProducerService.class);

    private final WebClient webClient;
    private final Random random = new Random();
    private final AtomicLong counter = new AtomicLong(0);

    @Value("${numbermaster.url:http://numbermaster:8081}")
    private String numberMasterUrl;

    public NumberProducerService() {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    @Scheduled(fixedRate = 2000) // Send a number every 2 seconds
    public void produceAndSendNumber() {
        // Generate a random number between 1 and 10000
        long number = random.nextInt(10000) + 1;

        logger.info("Producing number: {}", number);

        sendNumberToMaster(number);
    }

    private void sendNumberToMaster(long number) {
        NumberRequest request = new NumberRequest(number, counter.incrementAndGet());

        webClient.post()
                .uri(numberMasterUrl + "/api/numbers/evaluate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NumberResponse.class)
                .doOnSuccess(response -> handleResponse(response))
                .doOnError(error -> logger.error("Error sending number to NumberMaster: {}", error.getMessage()))
                .subscribe();
    }

    private void handleResponse(NumberResponse response) {
        if (response != null) {
            logger.info("received response : {}", response);
            //logger.info("Received response for number {}: isPrime={}, isMersenne={}, message='{}'",
            //        response.number(), response.isPrime(), response.isMersenne(), response.message());
        }
    }

    // DTOs

}
