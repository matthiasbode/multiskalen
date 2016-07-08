/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import applications.transshipment.generator.json.JsonPathSegment;
import applications.transshipment.generator.json.JsonTerminalResource;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hagedorn
 */
public class MouseListenerDraw implements EventHandler<MouseEvent> {

    double mousePosX, mousePosY;
    double mouseOldX, mouseOldY;
    double mouseDeltaX, mouseDeltaY;

    Point2D fixedPoint, fixedPointTransformed;
    Camera cameraGroup;

    Rectangle rect;
    LayoutEditor editor;

    public MouseListenerDraw(LayoutEditor editor) {
        this.cameraGroup = editor.cameraGroup;
        this.editor = editor;
    }

    @Override
    public void handle(MouseEvent event) {
        mousePosX = event.getX();
        mousePosY = event.getY();

        if (fixedPoint == null) {
            fixedPoint = new Point2D(mousePosX, mousePosY);
            fixedPointTransformed = cameraGroup.parentToLocal(fixedPoint);
        }

        if (rect == null) {
            rect = new Rectangle();
            cameraGroup.getChildren().add(rect);
        }

        if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            System.out.println("Mouse moved");

        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            System.out.println("Mouse dragged");

            cameraGroup.getChildren().remove(rect);

            Point2D draggedPointer = cameraGroup.parentToLocal(new Point2D(mousePosX, mousePosY));
            Point2D pUpperLeft = new Point2D(Math.min(draggedPointer.getX(), fixedPointTransformed.getX()), Math.min(draggedPointer.getY(), fixedPointTransformed.getY()));

            mouseDeltaX = Math.abs(fixedPointTransformed.getX() - draggedPointer.getX());
            mouseDeltaY = Math.abs(fixedPointTransformed.getY() - draggedPointer.getY());

            rect.setWidth(mouseDeltaX);
            rect.setHeight(mouseDeltaY);
            rect.setStyle("-fx-fill: linear-gradient(to bottom right, grey, black)");
            rect.setOpacity(0.4);
            rect.setX(pUpperLeft.getX());
            rect.setY(pUpperLeft.getY());
            rect.setTranslateZ(6);

            cameraGroup.getChildren().add(rect);

        } else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {

        } else if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            System.out.println("Mouse clicked");

            cameraGroup.getChildren().remove(rect);
            Point2D draggedPointer = cameraGroup.parentToLocal(new Point2D(mousePosX, mousePosY));
            Point2D pUpperLeft = new Point2D(Math.min(draggedPointer.getX(), fixedPointTransformed.getX()), Math.min(draggedPointer.getY(), fixedPointTransformed.getY()));

            mouseDeltaX = Math.abs(fixedPointTransformed.getX() - draggedPointer.getX());
            mouseDeltaY = Math.abs(fixedPointTransformed.getY() - draggedPointer.getY());

            LayoutElement element = new LayoutElement(
                    editor.boxDrawType.getSelectionModel().getSelectedItem(),
                    mouseDeltaY, mouseDeltaX, pUpperLeft.getX(), pUpperLeft.getY()
            );

            editor.elements.add(element);
            //warum?
            //editor.cameraGroup.getChildren().add(element);
            editor.refreshView();

            System.out.println("added LayoutElement");

            fixedPoint = null;
        }
    }

}
