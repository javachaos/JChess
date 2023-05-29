package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.moves.Move;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RandUtilsTest {

    private static final Logger LOGGER = LogManager.getLogger(RandUtils.class);

    @Test
    public void testRandUtils() {
        ExecUtils.ExecutionResult<List<Move>> r = ExecUtils.measureExecutionTime("Random Board",
                () -> BitUtils.pawnMovesWhite(BitUtils.createBitBoard(RandUtils.getRandomBoard()), null), 1000000);
        LOGGER.info(r.result());
        assertTrue(r.nanos() < TimeUnit.MILLISECONDS.toNanos(10));
    }
}
