package org.yoshi.numbermaker;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class RandomNumberMaker {
    private final List<Long> numbers = LongStream.rangeClosed(1, 1_000_000).boxed().collect(Collectors.toList());
    private final Iterator<Long> iterator;

    public RandomNumberMaker() {
        Collections.shuffle(numbers);
        iterator = numbers.iterator();
    }

    public long produceNextNumber() {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        throw new NoSuchElementException("No more unique numbers available");
    }

}
