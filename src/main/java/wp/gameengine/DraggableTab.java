package wp.gameengine;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;

public class DraggableTab extends Tab {
    // ======================================================
    // Static Members
    // ======================================================

    private static final String LABEL_CLASS = "draggable-tab-label";

    // ======================================================
    // Object Members
    // ======================================================

    private String labelText;

    private final Stage dragStage = new Stage();
    private final Label dragLabel = new Label();
    private final Label label = new Label();

    // ======================================================
    // Constructors
    // ======================================================

    public DraggableTab() {
        super();

        URL url = getClass().getResource("main.css");
        String style = Objects.requireNonNull(url).toExternalForm();

        initLabel();
        initDragStage(style);
    }

    private void initLabel() {
        dragLabel.getStyleClass().add(LABEL_CLASS);
        label.getStyleClass().add(LABEL_CLASS);
        label.setOnMouseDragged(this::mouseDragged);
        label.setOnMouseReleased(this::dragReleased);
        setGraphic(label);
    }

    private void initDragStage(String style) {

        Tab tab = new Tab();
        tab.setGraphic(dragLabel);
        tab.setContent(new StackPane());

        TabPane pane = new TabPane(tab);
        pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(pane);
        scene.getStylesheets().add(style);

        dragStage.initStyle(StageStyle.UNDECORATED);
        dragStage.setScene(scene);
        dragStage.setWidth(150);
        dragStage.setHeight(150);
    }

    // ======================================================
    // Static Methods
    // ======================================================

    private static DraggableTabPane getTabPane(Point2D pt) {
        for (DraggableTabPane pane : DraggableTabPane.getInstances()) {
            if (pane.getBounds().contains(pt)) return pane;
        }
        return null;
    }

    // ======================================================
    // Event Handlers
    // ======================================================

    private void mouseDragged(MouseEvent e) {
        dragStage.setX(e.getScreenX());
        dragStage.setY(e.getScreenY());
        dragStage.show();
    }

    private void dragReleased(MouseEvent e) {
        dragStage.hide();
    }

    // ======================================================
    // Object Methods
    // ======================================================

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.dragLabel.setText(labelText);
        this.label.setText(labelText);
        this.labelText = labelText;
    }
}
