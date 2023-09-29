package wp.gameengine;

import javafx.geometry.Bounds;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;

public class DraggableTabPane extends TabPane {

    private static final List<DraggableTabPane> instances = new ArrayList<>();

    public DraggableTabPane() {
        super();

        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        addInstance(this);
    }

    public Bounds getBounds() {
        Bounds local = getBoundsInLocal();
        return localToScreen(local);
    }

    public static List<DraggableTabPane> getInstances() {
        return instances;
    }

    private static void addInstance(DraggableTabPane instance) {
        DraggableTabPane.instances.add(instance);
    }
}
