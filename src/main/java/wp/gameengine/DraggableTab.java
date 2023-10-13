package wp.gameengine;

import javafx.collections.ListChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import wp.gameengine.exceptions.UnreachableException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DraggableTab extends Tab {
    // ======================================================
    // Static Members
    // ======================================================

    private static final String LABEL_CLASS = "draggable-tab-label";
    private static String STYLE;

    private static final Integer PREVIEW_SIZE = 150;
    private static final Integer DEFAULT_SIZE = 300;
    private static final Integer DEFAULT = 0;
    private static final Integer HEADER = 1;
    private static final Integer LEFT = 2;
    private static final Integer RIGHT = 3;
    private static final Integer BOTTOM = 4;

    private static final Stage dragStage = new Stage();
    private static final Label dragLabel = new Label();
    private static Integer dragPosition = -1;


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
        STYLE = Objects.requireNonNull(url).toExternalForm();

        Tab tab = new Tab();
        tab.setGraphic(dragLabel);
        TabPane pane = new TabPane(tab);
        pane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(STYLE);

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
            Bounds bounds = pane.getBounds();
            if (bounds != null && bounds.contains(pt)) return pane;
        }
        return null;
    }

    private static Bounds getDefaultBounds(Point2D pt) {
        dragPosition = DEFAULT;
        return new BoundingBox(pt.getX(), pt.getY(), PREVIEW_SIZE, PREVIEW_SIZE);
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

    private static void getHeaderBounds(DraggableTabPane pane) {
        dragPosition = HEADER;
        dragStage.hide();
        pane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
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
        dragPosition = LEFT;
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
        dragPosition = RIGHT;
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
        dragPosition = BOTTOM;
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

        getPane().setTabDragPolicy(TabPane.TabDragPolicy.FIXED);

        Bounds rect;
        if (pane == null) {
            rect = getDefaultBounds(pt);
        } else if (inHeaderBounds(getPane(), pt)) {
            getHeaderBounds(getPane());
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

        dragStage.setX(rect.getMinX());
        dragStage.setY(rect.getMinY());
        dragStage.setWidth(rect.getWidth());
        dragStage.setHeight(rect.getHeight());
        dragStage.show();
    }

    private void dragReleased(MouseEvent e) {
        dragStage.hide();

        if (Objects.equals(dragPosition, DEFAULT)) {
            defaultRelease(e);
        } else if (Objects.equals(dragPosition, LEFT)) {
            leftRelease(e);
        } else if (Objects.equals(dragPosition, RIGHT)) {
            rightRelease(e);
        }
    }

    private void defaultRelease(MouseEvent e) {
        getTabPane().getTabs().remove(this);

        Stage newStage = new Stage(StageStyle.UTILITY);

        DraggableTabPane newPane = new DraggableTabPane();
        newPane.getTabs().add(this);

        newStage.setOnHiding(event -> DraggableTabPane.removeInstance(newPane));
        newPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (newPane.getTabs().isEmpty()) newStage.hide();
        });

        Scene newScene = new Scene(newPane);
        newScene.getStylesheets().add(STYLE);

        newStage.setScene(newScene);
        newStage.setX(e.getScreenX());
        newStage.setY(e.getScreenY());
        newStage.setWidth(DEFAULT_SIZE);
        newStage.setHeight(DEFAULT_SIZE);
        newStage.show();
    }

    private void leftRelease(MouseEvent e) {
        Point2D pt = new Point2D(e.getScreenX(), e.getScreenY());
        DraggableTabPane pane = getTabPane(pt);
        if (pane == null) throw new UnreachableException("Impossible condition met.");

        DraggableTabPane oldPane = getPane();
        SplitPane oldSplit = oldPane.getParentSplit();

        SplitPane split = pane.nestSplit(Orientation.HORIZONTAL);

        List<Double> pos = new ArrayList<>();
        for (double position : split.getDividerPositions()) {
            pos.add(position);
        }

        int index = split.getItems().indexOf(pane);
        double rightDiv = split.getDividers().isEmpty() ? 1.0 : split.getDividerPositions()[index];
        double leftDiv = index == 0 ? 0.0 : split.getDividerPositions()[index - 1];
        double newDiv = ((rightDiv - leftDiv) / 3) + leftDiv;
        pos.add(index, newDiv);

        oldPane.getTabs().remove(this);
        DraggableTabPane newPane = new DraggableTabPane();
        newPane.getTabs().add(this);

        if (oldPane.getTabs().isEmpty()) {
            int oldPaneI = oldSplit.getItems().indexOf(oldPane);
            oldSplit.getItems().remove(oldPaneI);
            if (oldPaneI < index) index--;
            if (oldPaneI < pos.size()) {
                pos.remove(oldPaneI);
            }
        }

        split.getItems().add(index, newPane);
        for (int i = 0; i < pos.size(); i++) {
            System.out.printf("%d: %f%n", i, pos.get(i));
            split.setDividerPosition(i, pos.get(i));
        }
    }

    private void rightRelease(MouseEvent e) {
        Point2D pt = new Point2D(e.getScreenX(), e.getScreenY());
        DraggableTabPane pane = getTabPane(pt);
        if (pane == null) throw new UnreachableException("Impossible condition met.");

        DraggableTabPane oldPane = getPane();
        SplitPane oldSplit = oldPane.getParentSplit();

        SplitPane split = pane.nestSplit(Orientation.HORIZONTAL);

        List<Double> pos = new ArrayList<>();
        for (double position : split.getDividerPositions()) {
            pos.add(position);
        }

        int index = split.getItems().indexOf(pane) + 1;
        double rightDiv = split.getDividers().isEmpty() || index == split.getItems().size() ? 1.0 : split.getDividerPositions()[index - 1];
        double leftDiv = index == 1 ? 0.0 : split.getDividerPositions()[index - 2];
        double newDiv = rightDiv - ((rightDiv - leftDiv) / 3) ;
        pos.add(index-1, newDiv);

        oldPane.getTabs().remove(this);
        DraggableTabPane newPane = new DraggableTabPane();
        newPane.getTabs().add(this);


        if (oldPane.getTabs().isEmpty()) {
            int oldPaneI = oldSplit.getItems().indexOf(oldPane);
            oldSplit.getItems().remove(oldPaneI);
            if (oldPaneI < index) index--;
            if (oldPaneI < pos.size()) {
                pos.remove(oldPaneI);
            }
        }

        split.getItems().add(index, newPane);
        for (int i = 0; i < pos.size(); i++) {
            split.setDividerPosition(i, pos.get(i));
        }
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
