module com.github.javachaos.jchess.jchess {
    requires transitive org.apache.logging.log4j;

    requires java.logging;
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens com.github.javachaos.jchess to javafx.fxml;
    exports com.github.javachaos.jchess;
}