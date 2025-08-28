package numbermaker.dto;

public record NumberResponse(
        int number,
        int requestId,
        boolean isPrime,
        boolean isMersenne,
        String message) {
}
