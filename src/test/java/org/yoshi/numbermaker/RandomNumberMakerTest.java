package org.yoshi.numbermaker;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomNumberMakerTest {
    private RandomNumberMaker randomNumberMaker = new RandomNumberMaker();
    @Test
    void testGenerateUniqueNumbers() {
        List<Long> numbers = LongStream.generate(() -> randomNumberMaker.produceNextNumber())
                .limit(10000)
                .boxed()
                .collect(Collectors.toUnmodifiableList());

        Set<Long> uniqueNumbers = new HashSet<>(numbers);
        assertEquals(numbers.size(), uniqueNumbers.size(), "Generated numbers should be unique");
    }
}
