package wp.gameengine;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DraggableTab extends Tab {

    private final Stage window;
    private final Stage drag;

    private Label label;

    public DraggableTab() {
        this(null);
    }

    public DraggableTab(String title) {
        this(title, null);
    }

    public DraggableTab(String title, Node content) {
        super(title, content);

        Rectangle dummy = new Rectangle(3, 10, Color.web("#555555"));
        StackPane markerStack = new StackPane();
        markerStack.getChildren().add(dummy);
        window = new Stage(StageStyle.UNDECORATED);
        window.setScene(new Scene(markerStack));

        drag = new Stage(StageStyle.UNDECORATED);
    }
}
