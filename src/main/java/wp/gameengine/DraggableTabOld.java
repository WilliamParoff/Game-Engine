package wp.gameengine;

import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashSet;
import java.util.Set;

public class DraggableTabOld extends Tab {

    private static final Set<TabPane> tabPanes = new HashSet<>();

    private final Stage window;

    private final Text dragText;
    private final Stage drag;

    private Label label;

    public DraggableTabOld() {
        this(null);
    }

    public DraggableTabOld(String text) {
        this(text, null);
    }

    public DraggableTabOld(String text, Node content) {
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

        label = new Label("Some Text");
        setGraphic(label);

        label.setOnMouseDragged(event -> {
            System.out.println("Dragged");
            drag.setWidth(label.getWidth() + 10);
            drag.setHeight(label.getHeight() + 10);
            drag.setX(event.getScreenX());
            drag.setY(event.getScreenY());
            drag.show();

            Point2D screenPoint = new Point2D(event.getScreenX(), event.getScreenY());
            tabPanes.add(getTabPane());
            InsertData data = getInsertData(screenPoint);

            if (data == null || data.insertPane.getTabs().isEmpty()) {
                window.hide();
            } else {
                int index = data.index;
                boolean end = false;
                if (index == data.insertPane.getTabs().size()) {
                    end = true;
                    index--;
                }

                Rectangle2D rect = getAbsoluteRect(data.insertPane.getTabs().get(index));
                window.setX(end ? rect.getMaxX() + 13 : rect.getMinX());
                window.setY(rect.getMaxY() + 10);
                window.show();
            }
        });

        label.setOnMouseReleased(event -> {
            window.hide();
            drag.hide();
            if (!event.isStillSincePress()) {
                Point2D screenPoint = new Point2D(event.getScreenX(), event.getScreenY());
                TabPane oldTabPane = getTabPane();
                int oldIndex = oldTabPane.getTabs().indexOf(this);
                tabPanes.add(oldTabPane);

                InsertData data = getInsertData(screenPoint);
                if (data != null) {
                    int addIndex = data.index;
                    if (oldTabPane == data.insertPane && oldTabPane.getTabs().size() == 1) {
                        return;
                    }
                    oldTabPane.getTabs().remove(this);
                    if (oldIndex < addIndex && oldTabPane == data.insertPane) {
                        addIndex--;
                    }
                    if (addIndex > data.insertPane.getTabs().size()) {
                        addIndex = data.insertPane.getTabs().size();
                    }
                    data.insertPane.getTabs().add(addIndex, this);
                    data.insertPane.selectionModelProperty().get().select(addIndex);
                    return;
                }

                final Stage newStage = new Stage();
                final TabPane pane = new TabPane();
                tabPanes.add(pane);

                newStage.setOnHiding(stageEvent -> tabPanes.remove(pane));
                pane.getTabs().addListener((ListChangeListener<Tab>) c -> {
                    if (pane.getTabs().isEmpty()) {
                        newStage.hide();
                    }
                });

                newStage.setScene(new Scene(pane));
                newStage.initStyle(StageStyle.UTILITY);
                newStage.setX(event.getScreenX());
                newStage.setY(event.getScreenY());
                newStage.show();

                pane.requestLayout();
                pane.requestFocus();
            }
        });
    }

    private InsertData getInsertData(Point2D screenPoint) {
        for(TabPane tabPane : tabPanes) {
            Rectangle2D tabAbsolute = getAbsoluteRect(tabPane);
            if(tabAbsolute.contains(screenPoint)) {
                int tabInsertIndex = 0;
                if(!tabPane.getTabs().isEmpty()) {
                    Rectangle2D firstTabRect = getAbsoluteRect(tabPane.getTabs().get(0));
                    if(firstTabRect.getMaxY()+60 < screenPoint.getY() || firstTabRect.getMinY() > screenPoint.getY()) {
                        return null;
                    }
                    Rectangle2D lastTabRect = getAbsoluteRect(tabPane.getTabs().get(tabPane.getTabs().size() - 1));
                    if(screenPoint.getX() < (firstTabRect.getMinX() + firstTabRect.getWidth() / 2)) {
                        tabInsertIndex = 0;
                    }
                    else if(screenPoint.getX() > (lastTabRect.getMaxX() - lastTabRect.getWidth() / 2)) {
                        tabInsertIndex = tabPane.getTabs().size();
                    }
                    else {
                        for(int i = 0; i < tabPane.getTabs().size() - 1; i++) {
                            Tab leftTab = tabPane.getTabs().get(i);
                            Tab rightTab = tabPane.getTabs().get(i + 1);
                            if(leftTab instanceof FXTabsExample.DraggableTab && rightTab instanceof FXTabsExample.DraggableTab) {
                                Rectangle2D leftTabRect = getAbsoluteRect(leftTab);
                                Rectangle2D rightTabRect = getAbsoluteRect(rightTab);
                                if(betweenX(leftTabRect, rightTabRect, screenPoint.getX())) {
                                    tabInsertIndex = i + 1;
                                    break;
                                }
                            }
                        }
                    }
                }
                return new InsertData(tabInsertIndex, tabPane);
            }
        }
        return null;
    }

    private Rectangle2D getAbsoluteRect(Control node) {
        return new Rectangle2D(node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY()).getX() + node.getScene().getWindow().getX(),
                node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY()).getY() + node.getScene().getWindow().getY(),
                node.getWidth(),
                node.getHeight());
    }

    private Rectangle2D getAbsoluteRect(Tab tab) {
        Control node = ((DraggableTabOld) tab).getLabel();
        return getAbsoluteRect(node);
    }

    public Label getLabel() {
        return label;
    }

    private boolean betweenX(Rectangle2D r1, Rectangle2D r2, double xPoint) {
        double lowerBound = r1.getMinX() + r1.getWidth() / 2;
        double upperBound = r2.getMaxX() - r2.getWidth() / 2;
        return xPoint >= lowerBound && xPoint <= upperBound;
    }

    private record InsertData(int index, TabPane insertPane) {

    }
}
