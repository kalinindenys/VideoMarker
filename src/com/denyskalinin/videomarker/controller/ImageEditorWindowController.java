package com.denyskalinin.videomarker.controller;

import com.denyskalinin.videomarker.model.Layer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class ImageEditorWindowController {
    final private double BRUSH_MIN_SIZE = 10;
    final private double BRUSH_MAX_SIZE = 200;
    final private double BRUSH_DEFAULT_SIZE = 20;

    private Image imageToEdit;
    private String videoName;
    public static Color color;
    public static double brushSize;


    @FXML
    AnchorPane imageEditorAnchorPane;

    @FXML
    GridPane workspaceGridPane;

    @FXML
    Button saveButton;

    @FXML
    Button cancelButton;

    @FXML
    Button addLayerButton;

    @FXML
    Button deleteLayerButton;

    @FXML
    ColorPicker imageEditorColorPicker;

    @FXML
    Slider brushSizeSlider;

    @FXML
    TabPane imageTabPane;

    @FXML
    public void initialize() {
        color = imageEditorColorPicker.getValue();
        imageEditorColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            setColor(newValue);
        });
        brushSize = brushSizeSlider.getValue();
        brushSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            brushSize = newValue.doubleValue();
        });


        configureBrushSizeSlider();

    }

    private void configureBrushSizeSlider() {
        brushSizeSlider.setMin(BRUSH_MIN_SIZE);
        brushSizeSlider.setMax(BRUSH_MAX_SIZE);
        brushSizeSlider.setValue(BRUSH_DEFAULT_SIZE);
    }

    public void setImageToEdit(Image imageToEdit) {
        this.imageToEdit = imageToEdit;
    }

    public void setColor(Color color) {
        ImageEditorWindowController.color = color;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setImageData(Image image, String name) {
        setImageToEdit(image);
        setVideoName(name);
        addNewTab();
    }

    public void handleSaveButton() {
        String imageName = videoName + " " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".png";
        File savedImage = new File(imageName);
        // TODO: 26.10.2016 add filechooser
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(imageToEdit, null), "png", savedImage);
        } catch (IOException e) {
            System.out.println("lol");
        }


    }

    public void handleCancelButton() {
        Dialog confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm");
        confirmDialog.setContentText("Are you sure want to close image editor?");
        Optional<ButtonType> answer = confirmDialog.showAndWait();
        if (answer.isPresent() && answer.get().equals(ButtonType.OK)) {
            Stage currentStage = (Stage) imageEditorAnchorPane.getScene().getWindow();
            currentStage.close();
        }
    }

    public void handleAddLayerButton() {
        addNewTab();
    }

    public void handleDeleteLayerButton() {
        imageTabPane.getTabs().remove(imageTabPane.getSelectionModel().getSelectedIndex());
    }

    private void addNewTab() {
        String tabName = "Layer " + imageTabPane.getTabs().size();
        imageTabPane.getTabs().add(new Tab(tabName, new Layer(imageToEdit)));
    }


}
