package wp.gameengine;

import javafx.collections.ListChangeListener;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashSet;
import java.util.Set;

public class DraggableTabOld2 extends Tab {

    // ======================================================
    // Static members
    // ======================================================

    private static final int NO_INDEX = -1;
    private static final int LEFT_INSERT = -2;
    private static final int RIGHT_INSERT = -3;
    private static final int BOTTOM_INSERT = -4;

    private static final Set<TabPane> panes = new HashSet<>();

    // ======================================================
    // Object members
    // ======================================================

    private Stage preview;
    private Stage dragged;

    private Label label;
    private Text text;

    private String labelText;
    
    private TabPane insert;
    private int index;

    // ======================================================
    // Constructors
    // ======================================================

    public DraggableTabOld2() {
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
        double x = node.getScene().getWindow().getX();
        double y = node.getScene().getWindow().getY();
        double nodeMinX = node.getLayoutBounds().getMinX();
        double nodeMinY = node.getLayoutBounds().getMinY();
        double minX = node.localToScene(nodeMinX, nodeMinY).getX() + x;
        double minY = node.localToScene(nodeMinX, nodeMinY).getY() + y;
        return new Rectangle2D(minX, minY, node.getWidth(), node.getHeight());
    }

    private static Rectangle2D getAbsoluteRect(Tab tab) {
        Control node = ((DraggableTabOld2) tab).getLabel();
        return getAbsoluteRect(node);
    }

    private static Rectangle2D getLeftRect(TabPane pane) {
        Rectangle2D tabPaneAbs = getAbsoluteRect(pane);
        double width = tabPaneAbs.getWidth() / 4;
        double height = tabPaneAbs.getHeight() / 2;
        double minX = pane.getTranslateX();
        double minY = pane.getTranslateY() + (height / 2);
        return new Rectangle2D(minX, minY, width, height);
    }

    private static Rectangle2D getRightRect(TabPane pane) {
        Rectangle2D tabPaneAbs = getAbsoluteRect(pane);
        double width = tabPaneAbs.getWidth() / 4;
        double height = tabPaneAbs.getHeight() / 2;
        double minX = pane.getTranslateX() + pane.getWidth() - width;
        double minY = pane.getTranslateY() + (height / 2);
        return new Rectangle2D(minX, minY, width, height);
    }

    private static Rectangle2D getBottomRect(TabPane pane) {
        Rectangle2D tabPaneAbs = getAbsoluteRect(pane);
        double width = tabPaneAbs.getWidth() / 2;
        double height = tabPaneAbs.getHeight() / 4;
        double minX = tabPaneAbs.getMinX() - (tabPaneAbs.getWidth() / 4);
        double minY = tabPaneAbs.getMaxY() - height;
        return new Rectangle2D(minX, minY, width, height);
    }

    private static boolean betweenX(Rectangle2D r1, Rectangle2D r2, double xPoint) {
        double lowerBound = r1.getMinX() + r1.getWidth() / 2;
        double upperBound = r2.getMaxX() - r2.getWidth() / 2;
        return xPoint >= lowerBound && xPoint <= upperBound;
    }

    public static void addTabPane(TabPane pane) {
        DraggableTabOld2.panes.add(pane);
    }
    
    public static TabPane getTabPane(Point2D pos) {
        for (TabPane pane : panes) {
            Rectangle2D tabPaneAbs = getAbsoluteRect(pane);
            if (tabPaneAbs.contains(pos)) return pane;
        }
        return null;
    }

    public static int getInsertionIndex(TabPane pane, Point2D pos) {
        if (pane == null) return NO_INDEX;
        if (pane.getTabs().isEmpty()) return 0;

        if (getLeftRect(pane).contains(pos)) return LEFT_INSERT;
        if (getRightRect(pane).contains(pos)) return RIGHT_INSERT;
        if (getBottomRect(pane).contains(pos)) return BOTTOM_INSERT;

        Rectangle2D firstTabRect = getAbsoluteRect(pane.getTabs().get(0));
        if(firstTabRect.getMaxY()+60 < pos.getY() || firstTabRect.getMinY() > pos.getY()) {
            return NO_INDEX;
        }

        int lastTabIndex = pane.getTabs().size()-1;
        Rectangle2D lastTabRect = getAbsoluteRect(pane.getTabs().get(lastTabIndex));
        if (pos.getX() > (lastTabRect.getMaxX() - lastTabRect.getWidth() / 2)) {
            return pane.getTabs().size();
        }

        int insertIndex = 0;
        for (int i = 0; i < lastTabIndex; i++) {
            Tab leftTab = pane.getTabs().get(i);
            Tab rightTab = pane.getTabs().get(i + 1);
            if (leftTab instanceof DraggableTabOld2 && rightTab instanceof DraggableTabOld2) {
                Rectangle2D leftTabRect = getAbsoluteRect(leftTab);
                Rectangle2D rightTabRect = getAbsoluteRect(rightTab);
                if (betweenX(leftTabRect, rightTabRect, pos.getX())) {
                    insertIndex = i + 1;
                    break;
                }
            }
        }
        return insertIndex;
    }

    // ======================================================
    // Object methods
    // ======================================================

    private Stage initPreview() {
        Rectangle dummy = new Rectangle(0, 0);
        StackPane previewPane = new StackPane(dummy);
        previewPane.setStyle("-fx-background-color:#DDDDDD");

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

        insert = getTabPane(mousePos);
        index = getInsertionIndex(insert, mousePos);
        if (insert == null || index == NO_INDEX || insert.getTabs().isEmpty()) {
            preview.hide();
            return;
        }

        if (index > NO_INDEX) {
            boolean end = false;
            if (index == insert.getTabs().size()) {
                end = true;
                index--;
            }

            Rectangle2D rect = getAbsoluteRect(label);
            preview.setX(end ? rect.getMaxX() + 13 : rect.getMinX());
            preview.setY(rect.getMaxY() + 10);
            preview.setWidth(rect.getWidth());
            preview.setHeight(rect.getHeight());
            preview.show();
            return;
        }

        if (index == LEFT_INSERT) {
            Rectangle2D rect = getLeftRect(insert);
            preview.setX(rect.getMinX());
            preview.setY(rect.getMinY());
            preview.setWidth(rect.getWidth());
            preview.setHeight(rect.getHeight());
            preview.show();
            return;
        }

        if (index == RIGHT_INSERT) {
            Rectangle2D rect = getRightRect(insert);
            preview.setX(rect.getMinX());
            preview.setY(rect.getMinY());
            preview.setWidth(rect.getWidth());
            preview.setHeight(rect.getHeight());
            preview.show();
            return;
        }

        if (index == BOTTOM_INSERT) {
            Rectangle2D rect = getBottomRect(insert);
            preview.setX(rect.getMinX());
            preview.setY(rect.getMinY());
            preview.setWidth(rect.getWidth());
            preview.setHeight(rect.getHeight());
            preview.show();
        }
     }

    private void releasedEvent(MouseEvent event) {
        preview.hide();
        dragged.hide();

        if (event.isStillSincePress()) {
            return;
        }

        TabPane oldPane = getTabPane();
        int oldIndex = oldPane.getTabs().indexOf(this);

        if (insert == null) {
            noInsert(event);
        }

        if (index > NO_INDEX) {
            tabInsert(event, oldPane, oldIndex);
        }
    }

    private void noInsert(MouseEvent event) {
        final Stage newStage = new Stage();
        final TabPane pane = new TabPane();
        addTabPane(pane);

        newStage.setOnHiding(stageEvent -> panes.remove(pane));
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

    private void tabInsert(MouseEvent event, TabPane oldPane, int oldIndex) {
        if(oldPane == insert && oldPane.getTabs().size() == 1) {
            return;
        }
        oldPane.getTabs().remove(this);
        if(oldIndex < index && oldPane == insert) {
            index--;
        }
        if(index > insert.getTabs().size()) {
            index = insert.getTabs().size();
        }
        insert.getTabs().add(index, this);
        insert.selectionModelProperty().get().select(index);
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