package wp.gameengine;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DraggableTab extends Tab {

    private final Stage window;

    private final Text dragText;
    private final Stage drag;

    private Label label;

    public DraggableTab() {
        this(null);
    }

    public DraggableTab(String text) {
        this(text, null);
    }

    public DraggableTab(String text, Node content) {
        super(text, content);

        Rectangle dummy = new Rectangle(3, 10, Color.web("#555555"));
        StackPane markerStack = new StackPane();
        markerStack.getChildren().add(dummy);
        window = new Stage(StageStyle.UNDECORATED);
        window.setScene(new Scene(markerStack));

        dragText = new Text(text);

        StackPane dragPane = new StackPane();
        dragPane.setStyle("-fx-background-color:#DDDDDD");
        StackPane.setAlignment(dragText, Pos.CENTER);
        dragPane.getChildren().add(dragText);
        drag = new Stage(StageStyle.UNDECORATED);
        drag.setScene(new Scene(dragPane));

        label = new Label(text);
        setGraphic(label);

        label.setOnMouseDragged(event -> {
            drag.setWidth(label.getWidth() + 10);
            drag.setHeight(label.getHeight() + 10);
            drag.setX(event.getScreenX());
            drag.setY(event.getScreenY());
            drag.show();
        });
    }
}
