package com.denyskalinin.videomarker.model;

import com.denyskalinin.videomarker.controller.ImageEditorWindowController;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class Layer extends Pane {
    private Canvas imageLayer = new Canvas();
    private Canvas drawLayer = new Canvas();

    public Layer(Image image) {
        double width = image.getWidth();
        double height = image.getHeight();

        setWidth(width);
        setHeight(height);
        imageLayer.setWidth(width);
        imageLayer.setHeight(height);
        drawLayer.setWidth(width);
        drawLayer.setHeight(height);

        imageLayer.getGraphicsContext2D().drawImage(image, 0, 0);
        drawLayer.setOpacity(0.5);
        drawLayer.setCursor(Cursor.CROSSHAIR);

        getChildren().addAll(imageLayer, drawLayer);

        setOnMouseClicked(event -> {
            drawOnCanvas(this.getDrawLayer(), event);
        });
        setOnMouseDragged(event -> {
            drawOnCanvas(this.getDrawLayer(), event);
        });
    }

    public Canvas getDrawLayer() {
        return drawLayer;
    }

    public void drawOnCanvas(Canvas canvas, MouseEvent event) {
        GraphicsContext drawingItem = canvas.getGraphicsContext2D();
        drawingItem.setFill(ImageEditorWindowController.color);
        double brushSize = ImageEditorWindowController.brushSize;
        double brushCenter = brushSize / 2;
        drawingItem.fillOval(event.getX() - brushCenter, event.getY() - brushCenter, brushSize, brushSize);
    }
}
