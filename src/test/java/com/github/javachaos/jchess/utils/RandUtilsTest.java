package com.github.javachaos.jchess.utils;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandUtilsTest {

    @Test
    void testRandUtils() {
        ExecUtils.ExecutionResult<long[]> r = ExecUtils.measureExecutionTime("Random Board",
                () -> BitUtils.createBitBoard(RandUtils.getRandomBoard()), 10);
        BitUtils.prettyPrintBoard(r.result());
        assertTrue(r.nanos() < TimeUnit.MILLISECONDS.toNanos(100));
    }
}
