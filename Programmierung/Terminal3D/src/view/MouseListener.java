/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author hagedorn
 */
public class MouseListener implements EventHandler<MouseEvent> {

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private final Camera cameraGroup;
    private final View3D terminal;

    public MouseListener(View3D terminal) {
        this.cameraGroup = terminal.cameraGroup;
        this.terminal = terminal;
    }

    @Override
    public void handle(MouseEvent event) {

        if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            /**
             * Reset
             */
            if (event.isMiddleButtonDown()) {
                if (terminal.viewMode3D) {
                    terminal.setViewMode3D();
                } else {
                    terminal.setViewMode2D();
                }
            } else if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
                System.out.println("Mouse moved");

                if (event.isMiddleButtonDown()) {
                    if (terminal.viewMode3D) {
                        terminal.setViewMode3D();
                    } else {
                        terminal.setViewMode2D();
                    }
                }
            }
        } else if (event.getEventType()
                .equals(MouseEvent.MOUSE_DRAGGED)) {
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
            if (event.isPrimaryButtonDown()) {

                double tx = cameraGroup.t.getX();
                double ty = cameraGroup.t.getY();
                cameraGroup.t.setX(tx + (1 * mouseDeltaX));
                cameraGroup.t.setY(ty + (1 * mouseDeltaY));
            } 
            /**
             * Kippen
             */
            else if (event.isSecondaryButtonDown() && terminal.viewMode3D) {
                double ryAngle = cameraGroup.ry.getAngle();
                cameraGroup.ry.setAngle(ryAngle + (0.5)* mouseDeltaX);
                double rxAngle = cameraGroup.rx.getAngle();
                cameraGroup.rx.setAngle(rxAngle + (0.5)* mouseDeltaY);
            }
        }

    }

}
