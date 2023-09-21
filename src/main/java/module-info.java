module wp.gameengine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;

    opens wp.gameengine to javafx.fxml;
    exports wp.gameengine;
    exports wp.gameengine.controllers;
    opens wp.gameengine.controllers to javafx.fxml;
}