package com.example.battleship;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;


public class BattleShip extends Application {
    private ArrayList<Ship> ships;
    private final String IMG_PATH = "fondo4.png";
    private Teams winner;
    private AnchorPane vista;

    @Override
    public void start(Stage primaryStage) {
        vista = new AnchorPane();

        Image image = new Image(getClass().getResourceAsStream(IMG_PATH));
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(0, 0, true, true, true, true));
        vista.setBackground(new Background(backgroundImage));

        Button btn = new Button("INICIAR");
        btn.setPrefSize(150, 50);
        btn.setTextFill(Color.WHITE);
        btn.setBackground(new Background(new BackgroundFill(Color.GREEN, new CornerRadii(1), Insets.EMPTY)));
        AnchorPane.setTopAnchor(btn, 30d);
        AnchorPane.setLeftAnchor(btn, (vista.getWidth() - btn.getWidth()) / 2);
        AnchorPane.setRightAnchor(btn, (vista.getWidth() - btn.getWidth()) / 2);
        btn.setCursor(Cursor.HAND);

        ships = new ArrayList<>();
        winner = null;

        initializeShips();

        btn.setOnAction(event -> {
            ships.forEach(Ship::start);
            vista.getChildren().remove(btn);
        });

        vista.getChildren().add(btn);
        primaryStage.setTitle("Battle Ship");
        primaryStage.setScene(new Scene(vista, 1280, 800));
        primaryStage.show();

        new Thread(() -> {
            while (winner() == null) {
                int bRojos = 0;
                int bAzules = 0;
                for (Ship b : getShips()) {
                    if (b.team.equals(Teams.ROJO) && b.alive()) {
                        bRojos++;
                    }
                    if (b.team.equals(Teams.AZUL) && b.alive()) {
                        bAzules++;
                    }
                }
                if (bRojos == 0) {
                    setWinner(Teams.AZUL);
                }
                if (bAzules == 0) {
                    setWinner(Teams.ROJO);
                }
            }
            Platform.runLater(() -> {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(vista.getScene().getWindow());
                stage.setResizable(false);
                VBox layout = new VBox(20);
                layout.setPadding(new Insets(20));
                layout.setAlignment(Pos.CENTER);
                Label header = new Label("WINNER");
                Label content = new Label("TEAM " + winner().toString());
                layout.getChildren().addAll(header, content);
                Button closeButton = new Button("Cerrar");
                closeButton.setOnAction(e -> stage.close());
                layout.getChildren().add(closeButton);
                Scene scene = new Scene(layout);
                stage.setScene(scene);
                stage.showAndWait();
            });
        }).start();
    }

    private void initializeShips() {
        ships.add(new Acorazado(this, "AcorazadoRojo", Teams.ROJO, 600, 400));
        ships.add(new Lancha(this, "LanchaRoja", Teams.ROJO, 900, 100));
        ships.add(new Destructor(this, "DestructorRojo", Teams.ROJO, 900, 250));
        ships.add(new Submarino(this, "SubmarinoRojo", Teams.ROJO, 800, 480));
        ships.add(new Acorazado(this, "AcorazadoAzul", Teams.AZUL, 400, 400));
        ships.add(new Lancha(this, "LanchaAzul", Teams.AZUL, 100, 100));
        ships.add(new Destructor(this, "DestructorAzul", Teams.AZUL, 100, 250));
        ships.add(new Submarino(this, "SubmarinoAzul", Teams.AZUL, 200, 480));
    }
    public ArrayList<Ship> getShips() {
        return ships;
    }

    public Teams winner() {
        return winner;
    }

    public void setWinner(Teams winner) {
        this.winner = winner;
    }

    public AnchorPane getVista() {
        return vista;
    }

    public static void main(String[] args) {
        launch(args);
    }
}