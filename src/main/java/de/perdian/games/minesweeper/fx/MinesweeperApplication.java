package de.perdian.games.minesweeper.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MinesweeperApplication extends Application {

    private static final Logger log = LoggerFactory.getLogger(MinesweeperApplication.class);

    public static void main(String[] args) {
        log.info("Launching application");
        Application.launch(MinesweeperApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("Opening JavaFX stage");
        primaryStage.setScene(new Scene(new MinesweeperApplicationPane()));
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("Minesweeper");
        primaryStage.setWidth(Math.min(Screen.getPrimary().getBounds().getHeight() - 200, 1000));
        primaryStage.setHeight(Math.min(Screen.getPrimary().getBounds().getHeight() - 200, 700));
        primaryStage.show();
    }

}
