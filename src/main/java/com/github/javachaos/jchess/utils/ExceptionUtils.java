package com.github.javachaos.jchess.utils;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExceptionUtils {
    private ExceptionUtils() {
    }

    /**
     * Log the error and shutdown jvm.
     *
     * @param c the class for the logger.
     * @param e the exception thrown.
     */
    @SuppressWarnings("unused")
    public static void fatalError(final Class<?> c, final Exception e) {
        e.printStackTrace();
        logError(c, e);
        if (Platform.isFxApplicationThread()) {
            Platform.exit();
        }
        System.exit(0);
    }

    /**
     * Log the error.
     *
     * @param c the class for the logger.
     * @param e the exception thrown.
     */
    private static void logError(final Class<?> c, final Exception e) {
        Logger logger = LogManager.getLogger(c);
        logger.error(e.getMessage());
    }

    public static void log(final Exception e) {
        logError(e.getClass(), e);
    }

    @SuppressWarnings("unused")
    public static void log(final Class<?> c, final Exception e) {
        logError(c, e);
    }
}
