package wp.gameengine;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import wp.gameengine.exceptions.JavaFXFormatException;

import java.util.ArrayList;
import java.util.List;

public class DraggableTabPane extends TabPane {
    // ======================================================
    // Static members
    // ======================================================

    private static final List<DraggableTabPane> instances = new ArrayList<>();

    private static final String STYLE_CLASS = "draggable-tab-pane";

    // ======================================================
    // Object members
    // ======================================================

    // ======================================================
    // Constructors
    // ======================================================

    public DraggableTabPane() {
        super();

        getStyleClass().add(STYLE_CLASS);
        setTabDragPolicy(TabDragPolicy.REORDER);
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.FALSE.equals(newValue)) {
                removeInstance(this);
            }
        });

        addInstance(this);
    }

    // ======================================================
    // Static methods
    // ======================================================

    private static void addInstance(DraggableTabPane instance) {
        DraggableTabPane.instances.add(instance);
    }

    public static void removeInstance(DraggableTabPane instance) {
        DraggableTabPane.instances.remove(instance);
    }

    public static List<DraggableTabPane> getInstances() {
        return instances;
    }

    // ======================================================
    // Object methods
    // ======================================================

    public Bounds getBounds() {
        Bounds local = getBoundsInLocal();
        return localToScreen(local);
    }

    public Bounds getHeaderBounds() {
        StackPane header = (StackPane) lookup(".tab-header-area");
        Bounds local = header.getBoundsInLocal();
        return localToScreen(local);
    }

    public SplitPane getParentSplit() {
        for (Parent parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof SplitPane split) return split;
        }
        throw new JavaFXFormatException("Draggable tab pane requires at least one parent to be a split pane.");
    }

    public SplitPane getRootSplit() {
        SplitPane rootSplit = getParentSplit();
        for (Parent parent = rootSplit.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof SplitPane split) rootSplit = split;
        }
        return rootSplit;
    }

    public SplitPane nestSplit(Orientation orientation) {
        SplitPane split = getParentSplit();
        if (split.getOrientation().equals(orientation)) return split;

        SplitPane nestedSplit = new SplitPane();
        nestedSplit.setOrientation(orientation);

        int index = split.getItems().indexOf(this);
        split.getItems().add(index, nestedSplit);

        split.getItems().remove(this);
        nestedSplit.getItems().add(this);

        return nestedSplit;
    }
}
