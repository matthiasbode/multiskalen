/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

/**
 *
 * @author hagedorn
 */
public class MouseListenerPointer implements EventHandler<MouseEvent> {

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private final Camera cameraGroup;
    private LayoutElement selectedElement;
    private final LayoutEditor editor;

    public MouseListenerPointer(LayoutEditor editor) {
        this.cameraGroup = editor.cameraGroup;
        this.editor = editor;
    }

    @Override
    public void handle(MouseEvent event) {

        if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            mousePosX = 0;
            mousePosY = 0;

            editor.refreshView();
            editor.resetElementProperties();
            editor.selectedElement = null;
            
            /*
            Ein LayoutElement picken
            */
            PickResult pickResult = event.getPickResult();

            if (pickResult.getIntersectedNode() instanceof LayoutElement) {
                selectedElement = (LayoutElement) pickResult.getIntersectedNode();
                editor.boxElementType.getSelectionModel().select(selectedElement.getResource().getBezeichnung());
                editor.txtElementHeight.setText(String.valueOf(Math.round(selectedElement.getHeight())));
                editor.txtElementWidth.setText(String.valueOf(Math.round(selectedElement.getWidth())));
                editor.txtElementX.setText(String.valueOf(Math.round(selectedElement.getX())));
                editor.txtElementY.setText(String.valueOf(Math.round(selectedElement.getY())));

                selectedElement.setStyle("-fx-fill: linear-gradient(to bottom right, grey, black)");
                editor.selectedElement = selectedElement;
                editor.btnDelete.setVisible(true);
                editor.btnEdit.setVisible(true);
                editor.cameraGroup.getChildren().add(selectedElement.getTransformationRectangles());

            }
            Point2D p = new Point2D(event.getX(), event.getY());
            System.out.println("Screen:" + p);
            System.out.println("World:" + cameraGroup.parentToLocal(p));

        } else if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
            System.out.println("Mouse moved");
            if ((editor.selectedElement == null)) {
                mousePosX = event.getX();
                mousePosY = event.getY();
                mouseOldX = event.getX();
                mouseOldY = event.getY();
                Point2D p = new Point2D(mousePosX, mousePosY);
                System.out.println("Screen:" + p);
                System.out.println("World:" + cameraGroup.parentToLocal(p));
            }
        } else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            System.out.println("Mouse dragged");

            if (mousePosX == 0) {
                mouseOldX = event.getX();
                mouseOldY = event.getY();
            } else {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
            mousePosX = event.getX();
            mousePosY = event.getY();
            mouseDeltaX = mousePosX - mouseOldX;
            mouseDeltaY = mousePosY - mouseOldY;
            /**
             * Verschieben
             */
            if ((editor.selectedElement == null)) {
                if (event.isPrimaryButtonDown()) {
                    double tx = cameraGroup.t.getX();
                    double ty = cameraGroup.t.getY();
                    cameraGroup.t.setX(tx + (1 * mouseDeltaX));
                    cameraGroup.t.setY(ty + (1 * mouseDeltaY));
                } /**
                 * Reseten
                 */
                else if (event.isMiddleButtonDown()) {

//                cameraGroup.resetCam2D();
                }
                //Element verschieben
            } else {

                //double oldX = editor.selectedElement.getX();
                //double oldY = editor.selectedElement.getY();
                //editor.selectedElement.setX((oldX+(oldX-mousePosX)));
                //editor.selectedElement.setY((oldY+(mousePosY-oldY)));
                System.out.println("Fehler beim Verschieben");
            }
        } else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
            System.out.println("Mouse released");

        }

    }

}
