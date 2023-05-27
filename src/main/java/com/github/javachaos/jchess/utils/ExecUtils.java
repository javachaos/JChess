package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.exceptions.JChessRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class ExecUtils {

    public  record ExecutionResult<T>(T result, long nanos) {}
    private static final Logger LOGGER = LogManager.getLogger(ExecUtils.class);

    private ExecUtils() {}

    private static void jitWarmUp(Callable<?> runnable) {
        try {
            runnable.call();
            runnable.call();
            runnable.call();
            runnable.call();
            runnable.call();
            runnable.call();
            runnable.call();
            runnable.call();
            runnable.call();
            runnable.call();
        } catch (Exception e) {
            throw new JChessRuntimeException(e);
        }
    }

    public static <T> ExecutionResult<T> measureExecutionTime(String name, Callable<T> runnable, boolean warmup) {
        if (warmup) {
            jitWarmUp(runnable);
        }
        long startTime = System.nanoTime();
        T c;
        try {
            c = runnable.call();
        } catch (Exception e) {
            throw new JChessRuntimeException(e);
        }
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        LOGGER.info("Execution Time ({}): {} nanoseconds", name, executionTime);
        return new ExecutionResult<>(c, executionTime);
    }

}
