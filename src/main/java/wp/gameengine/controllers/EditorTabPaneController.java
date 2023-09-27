package wp.gameengine.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import wp.gameengine.DraggableTab;

public class EditorTabPaneController {

    @FXML
    private TabPane pane;

    public void initialize() {
        DraggableTab.addTabPane(pane);
    }
}
