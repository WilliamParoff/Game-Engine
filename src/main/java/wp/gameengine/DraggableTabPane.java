package wp.gameengine;

import javafx.geometry.Bounds;
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

        addInstance(this);
    }

    // ======================================================
    // Static methods
    // ======================================================

    private static void addInstance(DraggableTabPane instance) {
        DraggableTabPane.instances.add(instance);
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
}
