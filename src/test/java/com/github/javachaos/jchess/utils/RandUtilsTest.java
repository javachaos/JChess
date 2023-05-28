package com.github.javachaos.jchess.utils;

import org.junit.jupiter.api.Test;

@SuppressWarnings("all")
public class RandUtilsTest {

    @Test
    void testRandUtils() {
        long[] bits = BitUtils.createBitBoard(RandUtils.getRandomBoard());
        BitUtils.prettyPrintBoard(bits);
        //add some assertions
    }
}
