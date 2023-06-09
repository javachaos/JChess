module com.github.javachaos.jchess.jchess {
    requires transitive org.apache.logging.log4j;

    requires java.logging;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.github.javachaos.jchess to javafx.fxml;
    opens com.github.javachaos.jchess.gamelogic.pieces.core to com.google.gson;
    opens com.github.javachaos.jchess.gamelogic.pieces.impl to com.google.gson;
    exports com.github.javachaos.jchess;
    exports com.github.javachaos.jchess.gamelogic.saves to com.google.gson;
    opens com.github.javachaos.jchess.gamelogic.player to com.google.gson;
}