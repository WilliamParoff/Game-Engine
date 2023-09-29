package wp.gameengine;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashSet;
import java.util.Set;

public class DraggableTab extends Tab {
    // ======================================================
    // Static members
    // ======================================================

    // ======================================================
    // Objects members
    // ======================================================

    private Stage preview;
    private Stage dragged;

    private String labelText;
    private Label label;
    private Text text;

    private boolean addedTabPane = false;

    // ======================================================
    // Constructors and Constructor Methods
    // ======================================================

    public DraggableTab() {
        super();

        label = new Label();
        setGraphic(label);
        text = new Text();

        preview = initPreview();
        dragged = initDragged();

        label.setOnMouseDragged(this::draggedEvent);
        label.setOnMouseReleased(this::releasedEvent);
    }

    private Stage initPreview() {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color:#DDDDDD");
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(pane));
        return stage;
    }

    private Stage initDragged() {
        StackPane.setAlignment(text, Pos.CENTER);
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color:#DDDDDD");
        pane.getChildren().add(text);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(pane));
        return stage;
    }

    // ======================================================
    // Static methods
    // ======================================================

    private static Rectangle2D getAbsoluteRect(Control node) {
        double x = node.getScene().getWindow().getX();
        double y = node.getScene().getWindow().getY();
        double nodeMinX = node.getLayoutBounds().getMinX();
        double nodeMinY = node.getLayoutBounds().getMinY();
        double minX = node.localToScene(nodeMinX, nodeMinY).getX() + x;
        double minY = node.localToScene(nodeMinX, nodeMinY).getY() + y;
        return new Rectangle2D(minX, minY, node.getWidth(), node.getHeight());
    }

    private static Rectangle2D getAbsoluteRect(Tab tab) {
        Control node = ((DraggableTab) tab).getLabel();
        return getAbsoluteRect(node);
    }

    private static DraggableTabPane getTabPane(Point2D pos) {
        for (DraggableTabPane pane : DraggableTabPane.getInstances()) {
            if (pane.getBounds().contains(pos)) return pane;
        }
        return null;
    }

    // ======================================================
    // Object methods
    // ======================================================

    private void draggedEvent(MouseEvent event) {
        dragged.setWidth(label.getWidth() + 10);
        dragged.setHeight(label.getHeight() + 10);
        dragged.setX(event.getScreenX());
        dragged.setY(event.getScreenY());
        dragged.show();
        previewHandler(event);
    }

    private void releasedEvent(MouseEvent event) {
        preview.hide();
        dragged.hide();
    }

    private void previewHandler(MouseEvent event) {
        Point2D pos = new Point2D(event.getScreenX(), event.getScreenY());
        DraggableTabPane pane = getTabPane(pos);
        if (pane == null) return;

        Bounds rect = pane.getBounds();
        preview.setX(rect.getMinX());
        preview.setY(rect.getMinY());
        preview.setWidth(rect.getWidth());
        preview.setHeight(rect.getHeight());
        preview.show();
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
        this.label.setText(labelText);
        this.text.setText(labelText);
    }

    public Label getLabel() {
        return label;
    }
}
