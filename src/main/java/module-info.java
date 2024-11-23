module com.example.invaders {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires javafx.media;
    requires junit;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.slf4j;
    requires slf4j.api;

    opens com.example.invaders to javafx.fxml;
    exports com.example.invaders;
}