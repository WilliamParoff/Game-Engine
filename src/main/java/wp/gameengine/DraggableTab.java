package wp.gameengine;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Control;
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

    private Stage preview;
    private Stage dragged;

    private Label label;
    private Text text;

    private String labelText;

    // ======================================================
    // Constructors
    // ======================================================

    public DraggableTab() {
        super();

        preview = initPreview();
        dragged = initDragged();

        label = new Label();
        setGraphic(label);

        label.setOnMouseDragged(this::dragEvent);
        label.setOnMouseReleased(this::releasedEvent);
    }

    // ======================================================
    // Static methods
    // ======================================================

    private static Rectangle2D getAbsoluteRect(Control node) {
        return new Rectangle2D(node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY()).getX() + node.getScene().getWindow().getX(),
                node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY()).getY() + node.getScene().getWindow().getY(),
                node.getWidth(),
                node.getHeight());
    }

    private static Rectangle2D getAbsoluteRect(Tab tab) {
        Control node = ((DraggableTab) tab).getLabel();
        return getAbsoluteRect(node);
    }

    private static boolean betweenX(Rectangle2D r1, Rectangle2D r2, double xPoint) {
        double lowerBound = r1.getMinX() + r1.getWidth() / 2;
        double upperBound = r2.getMaxX() - r2.getWidth() / 2;
        return xPoint >= lowerBound && xPoint <= upperBound;
    }

    public static void addTabPane(TabPane pane) {
        DraggableTab.panes.add(pane);
    }
    
    public static TabPane getTabPane(Point2D pos) {
        for (TabPane pane : panes) {
            Rectangle2D tabPaneAbs = getAbsoluteRect(pane);
            if (tabPaneAbs.contains(pos)) return pane;
        }
        return null;
    }

    public static int getInsertionIndex(TabPane pane, Point2D pos) {
        int insertIndex = 0;
        if (!pane.getTabs().isEmpty()) {
            Rectangle2D firstTabRect = getAbsoluteRect(pane.getTabs().get(0));
            if(firstTabRect.getMaxY()+60 < pos.getY() || firstTabRect.getMinY() > pos.getY()) {
                return -1;
            }

            int lastTabIndex = pane.getTabs().size()-1;
            Rectangle2D lastTabRect = getAbsoluteRect(pane.getTabs().get(lastTabIndex));
            if (pos.getX() > (lastTabRect.getMaxX() - lastTabRect.getWidth() / 2)) {
                insertIndex = pane.getTabs().size();
            } else {
                for (int i = 0; i < lastTabIndex; i++) {
                    Tab leftTab = pane.getTabs().get(i);
                    Tab rightTab = pane.getTabs().get(i + 1);
                    if (leftTab instanceof DraggableTab && rightTab instanceof DraggableTab) {
                        Rectangle2D leftTabRect = getAbsoluteRect(leftTab);
                        Rectangle2D rightTabRect = getAbsoluteRect(rightTab);
                        if (betweenX(leftTabRect, rightTabRect, pos.getX())) {
                            insertIndex = i + 1;
                            break;
                        }
                    }
                }
            }
        }
        return insertIndex;
    }

    // ======================================================
    // Object methods
    // ======================================================

    private Stage initPreview() {
        Rectangle dummy = new Rectangle(3, 10, Color.RED);
        StackPane previewPane = new StackPane(dummy);

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(previewPane));
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

        Point2D mousePos = new Point2D(event.getScreenX(), event.getScreenY());

        TabPane insert = getTabPane(mousePos);
        if (insert == null || insert.getTabs().isEmpty()) {
            preview.hide();
            return;
        }

        boolean end = false;
        int index = getInsertionIndex(insert, mousePos);
        if (index < 0) {
            preview.hide();
            return;
        }

        if (index == insert.getTabs().size()) {
            end = true;
            index--;
        }

        Rectangle2D rect = getAbsoluteRect(insert.getTabs().get(index));
        preview.setX(end ? rect.getMaxX() + 13 : rect.getMinX());
        preview.setY(rect.getMaxY() + 10);
        preview.setWidth(rect.getWidth());
        preview.setHeight(rect.getHeight());
        preview.show();
     }

    private void releasedEvent(MouseEvent event) {
        preview.hide();
        dragged.hide();

        if (event.isStillSincePress()) {
            return;
        }

        Point2D pos = new Point2D(event.getScreenX(), event.getScreenY());

        TabPane oldPane = getTabPane();
        int oldIndex = oldPane.getTabs().indexOf(this);

        TabPane insert = getTabPane(pos);
        if (insert != null) {
            int addIndex = getInsertionIndex(insert, pos);
            if(oldPane == insert && oldPane.getTabs().size() == 1) {
                return;
            }
            oldPane.getTabs().remove(this);
            if(oldIndex < addIndex && oldPane == insert) {
                addIndex--;
            }
            if(addIndex > insert.getTabs().size()) {
                addIndex = insert.getTabs().size();
            }
            insert.getTabs().add(addIndex, this);
            insert.selectionModelProperty().get().select(addIndex);
            return;
        }
    }

    public String getLabelText() {
        return this.labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
        this.label.setText(labelText);
        this.text.setText(labelText);
    }

    public Label getLabel() {
        return this.label;
    }
}
