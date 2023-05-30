package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.logic.ChessBoard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PerformanceTest {
    private static final Logger LOGGER = LogManager.getLogger(PerformanceTest.class);

    @Test
    public void testPerformance() {
        ChessBoard cb = new ChessBoard();
        ExecUtils.ExecutionResult<Long> performanceTest = ExecUtils.measureExecutionTime(
                "Performance Test",
                () -> PerftUtils.perft(5, cb.getBits()), 0);
        LOGGER.info("Perft counter: {}", performanceTest.result());
        assertTrue(performanceTest.result() > 0);
    }
}
