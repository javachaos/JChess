package com.github.javachaos.jchess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JChessApplication extends Application {

    public static final Logger LOGGER = LogManager.getLogger(
            JChessApplication.class);

    /**
     * Run once upon JVM shutdown
     * WARNING: no long-running tasks allowed in this block.
     */
    public static void onShutdown() {
        LOGGER.info("Ending JChess.");
    }

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.info("Starting JChess");
        FXMLLoader fxmlLoader = new FXMLLoader(
                JChessController.class.getResource("jchess.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 512, 512);
        stage.setResizable(false);
        stage.setTitle("JChess");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        addShutdownHook();
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(JChessApplication::onShutdown));
    }
}