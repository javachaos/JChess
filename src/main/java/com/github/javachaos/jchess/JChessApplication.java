package com.github.javachaos.jchess;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class JChessApplication extends Application {

    public static final Logger LOGGER = LogManager.getLogger(
            JChessApplication.class);

    public static void main(String[] args) {
        launch();
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

}