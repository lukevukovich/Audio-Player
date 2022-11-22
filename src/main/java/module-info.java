module com.mp3player {
    requires javafx.controls;
    requires javafx.media;

    opens com.mp3player to javafx.fxml;
    exports com.mp3player;
}