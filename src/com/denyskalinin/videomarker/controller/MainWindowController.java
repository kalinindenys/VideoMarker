package com.denyskalinin.videomarker.controller;

import com.denyskalinin.videomarker.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.*;

public class MainWindowController {
    private String videoName;
    @FXML
    AnchorPane mainWindowAnchorPane;

    @FXML
    MediaView mainWindowMediaView;

    @FXML
    Button chooseFileButton;

    @FXML
    TextField chooseFileTextField;

    @FXML
    Slider videoSlider;

    @FXML
    Button playButton;

    @FXML
    Button pauseButton;

    @FXML
    Button stopButton;

    @FXML
    Button markButton;

    @FXML
    public void initialize() {
        markButton.setDisable(true);
    }

    public void handleChooseFileButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose video");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4", "*.mp4"));

        File file = fileChooser.showOpenDialog(mainWindowAnchorPane.getScene().getWindow());
        if (file != null) {
            String videoUri = file.toURI().toString();
            chooseFileTextField.setText(file.getPath());
            videoName = file.getName().replace(".mp4", "");
            if (mainWindowMediaView.getMediaPlayer() != null) {
                mainWindowMediaView.getMediaPlayer().stop();
            }
            Media video = new Media(videoUri);
            mainWindowMediaView.setMediaPlayer(new MediaPlayer(video));
            mainWindowMediaView.getMediaPlayer().play();
            mainWindowMediaView.getMediaPlayer().setOnReady(() -> {

                videoSlider.setMin(0);
                videoSlider.setValue(0);
                videoSlider.setMax(mainWindowMediaView.getMediaPlayer().getTotalDuration().toSeconds());

                mainWindowMediaView.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    videoSlider.setValue(newValue.toSeconds());
                });
            });
        }
    }

    public void handlePlayButton() {
        if (mainWindowMediaView.getMediaPlayer() != null && mainWindowMediaView.getMediaPlayer().getMedia() != null) {
            mainWindowMediaView.getMediaPlayer().play();
            markButton.setDisable(true);
        }
    }

    public void handlePauseButton() {
        if (mainWindowMediaView.getMediaPlayer() != null && mainWindowMediaView.getMediaPlayer().getMedia() != null) {
            mainWindowMediaView.getMediaPlayer().pause();
            markButton.setDisable(false);
        }

    }

    public void handleStopButton() {
        if (mainWindowMediaView.getMediaPlayer() != null && mainWindowMediaView.getMediaPlayer().getMedia() != null) {
            mainWindowMediaView.getMediaPlayer().stop();
            markButton.setDisable(true);
        }
    }

    public void handleVideoSlider() {
        mainWindowMediaView.getMediaPlayer().seek(Duration.seconds(videoSlider.getValue()));
    }

    public void handleMarkButton() {
        WritableImage writableImage = mainWindowMediaView.snapshot(new SnapshotParameters(), null);

        try {
            openImageEditorWindow(writableImage);
        } catch (IOException e) {
            System.out.println("lol");
        }
    }

    public void openImageEditorWindow(Image image) throws IOException {
        videoName += mainWindowMediaView.getMediaPlayer().getCurrentTime().toSeconds();
        Stage imageEditorStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/ImageEditorWindow.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        imageEditorStage.setTitle("Image Editor");
        imageEditorStage.setScene(scene);
        imageEditorStage.show();

        ImageEditorWindowController controller = loader.getController();
        controller.setImageData(image, videoName);

    }
}
