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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static com.denyskalinin.videomarker.controller.MainWindowController.defaultDirectory;

public class ImageEditorWindowController {
    final private double BRUSH_MIN_SIZE = 10;
    final private double BRUSH_MAX_SIZE = 200;
    final private double BRUSH_DEFAULT_SIZE = 20;

    public static double brushSize;
    public static Color color;

    private Image imageToEdit;
    private String videoName;


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
        setColorValueAndListener();
        setBrushSizeValueAndListener();
        configureBrushSizeSlider();
    }

    private void setColorValueAndListener() {
        setColor(imageEditorColorPicker.getValue());
        imageEditorColorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            setColor(newValue);
        });
    }

    private void setBrushSizeValueAndListener() {
        brushSize = brushSizeSlider.getValue();
        brushSizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            brushSize = newValue.doubleValue();
        });
    }

    private void configureBrushSizeSlider() {
        brushSizeSlider.setMin(BRUSH_MIN_SIZE);
        brushSizeSlider.setMax(BRUSH_MAX_SIZE);
        brushSizeSlider.setValue(BRUSH_DEFAULT_SIZE);
    }

    private void setImageToEdit(Image imageToEdit) {
        this.imageToEdit = imageToEdit;
    }

    private void setColor(Color color) {
        ImageEditorWindowController.color = color;
    }

    private void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setImageData(Image image, String name) {
        setImageToEdit(image);
        setVideoName(name);
        addNewTab();
    }

    public void handleSaveButton() throws IOException {
        File choosedFile = showDirectoryChooser();
        if (choosedFile != null) {
            defaultDirectory = choosedFile;
            String imageName = videoName + " " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + ".png";
            saveImage(imageName, imageToEdit);

            for (int i = 0; i < imageTabPane.getTabs().size(); i++) {
                String layerName = "layer " + i + " " + imageName;
                Image layerImage = imageTabPane.getTabs().get(i).getContent().snapshot(new SnapshotParameters(), null);
                saveImage(layerName, layerImage);
            }
        }
    }

    private File showDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Save images...");
        directoryChooser.setInitialDirectory(defaultDirectory);
        return directoryChooser.showDialog(imageEditorAnchorPane.getScene().getWindow());
    }

    private void saveImage(String imageName, Image image) throws IOException {
        File savedImage = new File(defaultDirectory.getAbsolutePath() + "\\" + imageName);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", savedImage);
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
        if (imageTabPane.getTabs().size() != 0) {
            imageTabPane.getTabs().remove(imageTabPane.getSelectionModel().getSelectedIndex());
        }
    }

    private void addNewTab() {
        String tabName = "Layer " + imageTabPane.getTabs().size();
        imageTabPane.getTabs().add(new Tab(tabName, new Layer(imageToEdit)));
    }


}
