package it.unibo.pcd.assignment2.reactive.view.impl;

import it.unibo.pcd.assignment2.reactive.controller.Controller;
import it.unibo.pcd.assignment2.reactive.controller.impl.ControllerImpl;
import it.unibo.pcd.assignment2.reactive.view.View;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * An implementation of the {@link View} interface which creates a Graphical User Interface using JavaFX.
 */
public class GUIView implements View {
    private static final String STOPWORDS_FILE_ERROR_MSG = "Select a file containing the stopwords";
    private static final String PDFS_FOLDER_ERROR_MSG = "Select a folder for your PDF files";
    private static final String FXML_FILE_ERROR_MSG = "An error occurred while loading configuration files: %s";
    private static final String APP_TITLE = "Unique words counter";
    private static final String FXML_FILENAME = "main.fxml";
    private static final String PROCESSED_WORDS_LABEL_MSG = "Processed words: %d";
    private static final String PDFS_FOLDER_CHOOSER_TITLE = "Choose directory with PDFs files";
    private static final String INITIAL_DIRECTORY = System.getProperty("user.home");
    private static final String STOPWORDS_FILE_CHOOSER_TITLE = "Choose stopwords file";
    private static final String FILE_CHOOSER_DESC = "Text files";
    private static final String FILE_CHOOSER_TXT_EXT = "*.txt";
    private static final String FILE_NOT_CHOSEN_TEXT = "Select file...";
    private static final String SUSPEND_BUTTON_TEXT = "Suspend";
    private static final String RESUME_BUTTON_TEXT = "Resume";

    private final Controller controller;
    private final Stage primaryStage;
    private boolean isSuspended;
    private Optional<Path> filesDirectoryPath;
    private Optional<Path> stopwordsFilePath;
    @FXML
    private BarChart<String, Long> barChart;
    @FXML
    private Label filesDirectoryLabel;
    @FXML
    private Button filesDirectoryButton;
    @FXML
    private Spinner<Integer> numberWordsSpinner;
    @FXML
    private Label stopwordsFileLabel;
    @FXML
    private Button stopwordsFileButton;
    @FXML
    private Button startButton;
    @FXML
    private Button suspendButton;
    @FXML
    private Button resetButton;
    @FXML
    private Label processedWordsLabel;

    /**
     * Default constructor.
     * @param primaryStage the JavaFX stage object in which drawing the scene of the interface
     */
    public GUIView(final Stage primaryStage) {
        this.controller = new ControllerImpl(this);
        this.primaryStage = Objects.requireNonNull(primaryStage);
        this.filesDirectoryPath = Optional.empty();
        this.stopwordsFilePath = Optional.empty();
        this.isSuspended = false;
        this.show();
    }

    @Override
    public void displayProgress(final Map<String, Long> frequencies, final long processedWords) {
        Platform.runLater(() -> {
            final ObservableList<XYChart.Series<String, Long>> data = this.barChart.getData();
            data.clear();
            this.barChart.layout();
            final XYChart.Series<String, Long> series = new XYChart.Series<>();
            frequencies.entrySet()
                       .stream()
                       .map(e -> new XYChart.Data<>(e.getKey(), e.getValue()))
                       .forEach(d -> series.getData().add(d));
            data.add(series);
            this.processedWordsLabel.setText(String.format(PROCESSED_WORDS_LABEL_MSG, processedWords));
        });
    }

    @Override
    public void displayCompletion() {
        Platform.runLater(() -> {
            this.suspendButton.setDisable(true);
            this.resetButton.setDisable(false);
        });
    }

    @Override
    public void displayError(final String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait());
    }

    /*
     * It completes the GUI initialization and it shows the view to the user.
     */
    private void show() {
        this.filesDirectoryPath = Optional.empty();
        this.stopwordsFilePath = Optional.empty();
        try {
            final FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(FXML_FILENAME));
            loader.setController(this);
            final BorderPane borderPane = loader.load();
            this.setFilesDirectoryControls();
            this.setStopwordsFileControls();
            this.startButton.setOnMouseClicked(e -> {
                if (this.filesDirectoryPath.isPresent()) {
                    if (this.stopwordsFilePath.isPresent()) {
                        this.startButton.setDisable(true);
                        this.suspendButton.setDisable(false);
                        this.controller.launch(this.filesDirectoryPath.get(),
                                               this.stopwordsFilePath.get(),
                                               this.numberWordsSpinner.getValue());
                    } else {
                        this.displayError(STOPWORDS_FILE_ERROR_MSG);
                    }
                } else {
                    this.displayError(PDFS_FOLDER_ERROR_MSG);
                }
            });
            this.suspendButton.setOnMouseClicked(e -> {
                if (this.isSuspended) {
                    this.suspendButton.setText(SUSPEND_BUTTON_TEXT);
                    this.controller.resume();
                } else {
                    this.controller.suspend();
                    this.suspendButton.setText(RESUME_BUTTON_TEXT);
                }
                this.isSuspended = !this.isSuspended;
            });
            this.resetButton.setOnMouseClicked(e -> {
                this.barChart.getData().clear();
                this.processedWordsLabel.setText(String.format(PROCESSED_WORDS_LABEL_MSG, 0));
                this.stopwordsFileLabel.setText(FILE_NOT_CHOSEN_TEXT);
                this.filesDirectoryLabel.setText(FILE_NOT_CHOSEN_TEXT);
                this.startButton.setDisable(false);
                this.suspendButton.setDisable(true);
                this.resetButton.setDisable(true);
            });
            final Scene scene = new Scene(borderPane);
            this.primaryStage.setScene(scene);
            this.primaryStage.sizeToScene();
            this.primaryStage.setTitle(APP_TITLE);
            this.primaryStage.setOnCloseRequest(e -> this.controller.exit());
            this.primaryStage.show();
            this.primaryStage.centerOnScreen();
            this.primaryStage.setMinWidth(this.primaryStage.getWidth());
            this.primaryStage.setMinHeight(this.primaryStage.getHeight());
        } catch (final IOException ex) {
            this.displayError(String.format(FXML_FILE_ERROR_MSG, ex.getMessage()));
        }
    }

    /*
     * It sets the group of controls about the PDF directory.
     */
    private void setFilesDirectoryControls() {
        this.filesDirectoryButton.setOnMouseClicked(e -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(PDFS_FOLDER_CHOOSER_TITLE);
            directoryChooser.setInitialDirectory(new File(INITIAL_DIRECTORY));
            final File file = directoryChooser.showDialog(this.primaryStage);
            this.filesDirectoryPath = Optional.ofNullable(file).map(File::toPath);
            if (file != null) {
                this.filesDirectoryLabel.setText(file.toString());
            }
        });
    }

    /*
     * It sets the group of controls about the stopwords file.
     */
    private void setStopwordsFileControls() {
        this.stopwordsFileButton.setOnMouseClicked(e -> {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(STOPWORDS_FILE_CHOOSER_TITLE);
            fileChooser.setInitialDirectory(new File(INITIAL_DIRECTORY));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(FILE_CHOOSER_DESC, FILE_CHOOSER_TXT_EXT));
            final File file = fileChooser.showOpenDialog(this.primaryStage);
            this.stopwordsFilePath = Optional.ofNullable(file).map(File::toPath);
            if (file != null) {
                this.stopwordsFileLabel.setText(file.toString());
            }
        });
    }
}
