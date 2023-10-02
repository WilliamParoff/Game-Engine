package wp.gameengine;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;

public class DraggableTab extends Tab {
    // ======================================================
    // Static Members
    // ======================================================

    private static final Stage dragStage = new Stage();
    private static final Label dragLabel = new Label();

    private static final String LABEL_CLASS = "draggable-tab-label";

    private static final Integer DEFAULT_SIZE = 150;

    // ======================================================
    // Object Members
    // ======================================================

    private String labelText;

    private final Label label = new Label();

    // ======================================================
    // Constructors
    // ======================================================

    static {
        URL url = DraggableTab.class.getResource("main.css");
        String style = Objects.requireNonNull(url).toExternalForm();

        Tab tab = new Tab();
        tab.setGraphic(dragLabel);
        TabPane pane = new TabPane(tab);
        pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(style);

        dragStage.initStyle(StageStyle.UNDECORATED);
        dragStage.setScene(scene);
    }

    public DraggableTab() {
        super();
        initLabel();
    }

    private void initLabel() {
        dragLabel.getStyleClass().add(LABEL_CLASS);
        label.getStyleClass().add(LABEL_CLASS);
        label.setOnDragDetected(this::dragEntered);
        label.setOnMouseDragged(this::mouseDragged);
        label.setOnMouseReleased(this::dragReleased);
        setGraphic(label);
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

    private static Bounds getDefaultBounds(Point2D pt) {
        return new BoundingBox(pt.getX(), pt.getY(), DEFAULT_SIZE, DEFAULT_SIZE);
    }

    private static boolean inHeaderBounds(DraggableTabPane pane, Point2D pt) {
        Bounds headerBounds = pane.getHeaderBounds();
        double minX = headerBounds.getMinX();
        double minY = headerBounds.getMinY();
        double width = headerBounds.getWidth();
        double height = headerBounds.getHeight() * 1.5;
        Bounds bounds = new BoundingBox(minX, minY, width, height);
        return bounds.contains(pt);
    }

    private static boolean inLeftBounds(DraggableTabPane pane, Point2D pt) {
        Bounds paneBounds = pane.getBounds();
        double minX = paneBounds.getMinX();
        double minY = paneBounds.getMinY();
        double width = paneBounds.getWidth() / 4;
        double height = paneBounds.getHeight();
        Bounds bounds = new BoundingBox(minX, minY, width, height);
        return bounds.contains(pt);
    }

    private static Bounds getLeftBounds(DraggableTabPane pane) {
        Bounds paneBounds = pane.getBounds();
        double minX = paneBounds.getMinX();
        double minY = paneBounds.getMinY();
        double width = paneBounds.getWidth() / 3;
        double height = paneBounds.getHeight();
        return new BoundingBox(minX, minY, width, height);
    }

    private static boolean inRightBounds(DraggableTabPane pane, Point2D pt) {
        Bounds paneBounds = pane.getBounds();
        double width = paneBounds.getWidth() / 4;
        double height = paneBounds.getHeight();
        double minX = paneBounds.getMaxX() - width;
        double minY = paneBounds.getMinY();
        Bounds bounds = new BoundingBox(minX, minY, width, height);
        return bounds.contains(pt);
    }

    private static Bounds getRightBounds(DraggableTabPane pane) {
        Bounds paneBounds = pane.getBounds();
        double width = paneBounds.getWidth() / 3;
        double height = paneBounds.getHeight();
        double minX = paneBounds.getMaxX() - width;
        double minY = paneBounds.getMinY();
        return new BoundingBox(minX, minY, width, height);
    }

    private static boolean inBottomBounds(DraggableTabPane pane, Point2D pt) {
        Bounds paneBounds = pane.getBounds();
        double width = paneBounds.getWidth();
        double height = paneBounds.getHeight() / 4;
        double minX = paneBounds.getMinX();
        double minY = paneBounds.getMaxY() - height;
        Bounds bounds = new BoundingBox(minX, minY, width, height);
        return bounds.contains(pt);
    }

    private static Bounds getBottomBounds(DraggableTabPane pane) {
        Bounds paneBounds = pane.getBounds();
        double width = paneBounds.getWidth();
        double height = paneBounds.getHeight() / 3;
        double minX = paneBounds.getMinX();
        double minY = paneBounds.getMaxY() - height;
        return new BoundingBox(minX, minY, width, height);
    }

    // ======================================================
    // Event Handlers
    // ======================================================

    private void dragEntered(MouseEvent e) {
        dragLabel.setText(labelText);
    }

    private void mouseDragged(MouseEvent e) {
        Point2D pt = new Point2D(e.getScreenX(), e.getScreenY());
        DraggableTabPane pane = getTabPane(pt);

        Bounds rect;
        if (pane == null) {
            rect = getDefaultBounds(pt);
        } else if (inHeaderBounds(pane, pt)) {
            if (pane != getPane()) {
                pane.getTabs().add(this);
                getPane().getTabs().remove(this);
                MouseEvent.fireEvent(label, e);
            }

            pane.setTabDragPolicy(TabDragPolicy.REORDER);
            dragStage.hide();
            return;
        } else if (inLeftBounds(pane, pt)) {
            rect = getLeftBounds(pane);
        } else if (inRightBounds(pane, pt)) {
            rect = getRightBounds(pane);
        } else if (inBottomBounds(pane, pt)) {
            rect = getBottomBounds(pane);
        } else {
            rect = getDefaultBounds(pt);
        }

        if (pane != null) {
            pane.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
        }

        dragStage.setX(rect.getMinX());
        dragStage.setY(rect.getMinY());
        dragStage.setWidth(rect.getWidth());
        dragStage.setHeight(rect.getHeight());
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

    public DraggableTabPane getPane() {
        return (DraggableTabPane) getTabPane();
    }
}
