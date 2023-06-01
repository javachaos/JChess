package com.github.javachaos.jchess.utils;

import com.github.javachaos.jchess.exceptions.JChessRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class ExecUtils {
    static class ExecutionResult<T> {
        private final T result;
        private final long nanos;
        public ExecutionResult(T result, long nanos) {
            this.result = result;
            this.nanos = nanos;
        }

        public long nanos() {
            return nanos;
        }

        public T result() {
            return result;
        }
    }
    private static final Logger LOGGER = LogManager.getLogger(ExecUtils.class);

    private ExecUtils() {}

    private static void jitWarmUp(Callable<?> runnable, int warmupCount) {
        try {
            for (int i = 0; i < warmupCount; i++) {
                runnable.call();
            }
        } catch (Exception e) {
            throw new JChessRuntimeException(e);
        }
    }

    public static <T> ExecutionResult<T> measureExecutionTime(String name, Callable<T> runnable, int warmupCount) {
        if (warmupCount > 0) {
            jitWarmUp(runnable, warmupCount);
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
