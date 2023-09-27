package wp.gameengine;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Builder;

import java.util.HashSet;
import java.util.Set;

public class DraggableTab extends Tab {

    // ======================================================
    // Static members
    // ======================================================

    private static final Set<TabPane> panes = new HashSet<>();

    // ======================================================
    // Object members
    // ======================================================

    private Stage window;
    private Stage dragged;

    private Label label;
    private Text text;

    private String labelText;

    // ======================================================
    // Constructors
    // ======================================================

    public DraggableTab() {
        super();

        window = initWindow();
        dragged = initDragged();

        label = new Label();
        setGraphic(label);

        label.setOnMouseDragged(this::dragEvent);
        label.setOnMouseReleased(this::releasedEvent);
    }

    // ======================================================
    // Static methods
    // ======================================================

    public static void addTabPane(TabPane pane) {
        DraggableTab.panes.add(pane);
    }

    // ======================================================
    // Object methods
    // ======================================================

    private Stage initWindow() {
        Rectangle dummy = new Rectangle(3, 10, Color.RED);
        StackPane windowPane = new StackPane(dummy);

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(windowPane));
        return stage;
    }

    private Stage initDragged() {
        text = new Text();
        StackPane dragPane = new StackPane();
        dragPane.setStyle("-fx-background-color:#DDDDDD");
        StackPane.setAlignment(text, Pos.CENTER);
        dragPane.getChildren().add(text);

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(dragPane));
        return stage;
    }

    private void dragEvent(MouseEvent event) {
        dragged.setWidth(label.getWidth() + 10);
        dragged.setHeight(label.getHeight() + 10);
        dragged.setX(event.getScreenX());
        dragged.setY(event.getScreenY());
        dragged.show();

        Point2D point = new Point2D(event.getScreenX(), event.getScreenY());
        
    }

    private void releasedEvent(MouseEvent event) {
        dragged.hide();

    }

    public String getLabelText() {
        return this.labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
        this.label.setText(labelText);
        this.text.setText(labelText);
    }
}
