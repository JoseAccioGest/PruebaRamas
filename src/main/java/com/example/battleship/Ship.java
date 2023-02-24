package com.example.battleship;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

public class Ship extends Thread{
    VBox vBox;
    private BattleShip game;
    private String name;
    double life, maxLife;
    double damage;
    double precision;
    Teams team;
    ImageView image;
    private double sonar;
    private double x;
    private double y;
    private double x2;
    private double y2;
    private double inclination = 45;
    private ProgressBar lifeBar;
    long wait;
    public Ship(BattleShip game, String name, Teams team, String rutaImg, int sonar, double velocidad, double x, double y, double vida, double damage,
                double precision, int tamanno, long wait) {
        this.y = y;
        this.damage = damage;
        this.precision = precision;
        this.game = game;
        this.sonar = sonar;
        this.name = name;
        this.team = team;
        this.life = vida;
        this.maxLife = vida;
        this.x = x;
        x2 = -velocidad;
        y2 = -velocidad;
        this.wait = wait;

        image = new ImageView(new Image(BattleShip.class.getResource(rutaImg).toExternalForm()));
        image.setPreserveRatio(true);
        image.setFitWidth(tamanno);

        vBox = new VBox();
        lifeBar = new ProgressBar();
        lifeBar.setProgress(1);
        lifeBar.setPrefHeight(10);
        lifeBar.setPrefWidth(tamanno);
        String color = team.equals(Teams.ROJO) ? "RED": "BLUE";
        lifeBar.setStyle("-fx-accent: "+color+";");
        vBox.getChildren().addAll(lifeBar,image);
        vBox.setTranslateX(x);
        vBox.setTranslateY(y);
        vBox.setRotate(inclination);
    }

    private void shoot(Ship enemigo) throws MalformedURLException {
        Disparo disparo = new Disparo(game,this,enemigo);
        disparo.start();
    }

    public static double distance(double x, double y, double xx, double yy){
        double xDistancia = xx -x;
        double yDistancia = yy - y;
        return Math.sqrt(Math.pow(xDistancia,2) + Math.pow(yDistancia,2));
    }

    private synchronized void getDamage(double danno){
        life = life - danno;
        if(life < 1){
            life = 0;
        }
    }
    private void moveShip() {
        Timeline time = new Timeline();
        time.setCycleCount(1);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(40), event -> {
            setX(x+ x2);
            setY(y+y2);
            vBox.setTranslateX(vBox.getTranslateX() + x2);
            vBox.setTranslateY(vBox.getTranslateY() + y2);

            Bounds bounds = vBox.getBoundsInParent();
            if (bounds.getMaxX() >= 1280 || bounds.getMinX() <= 0) {
                x2 = -x2;
                if(random(0,1) == 1){
                    y2 = -y2;
                    inclination = 180 + inclination;
                }else{
                    inclination = 180 - inclination;
                }
                vBox.setRotate(inclination);
            }
            if (bounds.getMaxY() >= 800 || bounds.getMinY() <= 0) {
                y2 = -y2;
                if(random(0,1) == 1){
                    x2 = -x2;
                    inclination = 180 + inclination;
                }else{
                    inclination = 360 - inclination;
                }
                vBox.setRotate(inclination);
            }
            if(alive()){
                moveShip();
            }
        });

        time.getKeyFrames().add(keyFrame);
        time.play();
    }

    private Thread reload(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(wait);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    public static int random(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }


    public synchronized void getShoot(double danno){
        getDamage(danno);
        lifeBar.setProgress(life / maxLife);
    }

    private Thread detect() {
        return new Thread(() -> {
            while (alive() && game.winner() == null) {
                List<Ship> barcos = game.getShips();
                detectShip(barcos);
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> game.getVista().getChildren().remove(vBox));
        });
    }

    private void detectShip(List<Ship> ships) {
        for (Ship barco : ships) {
            if (distance(getX(), getY(), barco.getX(), barco.getY()) < sonar &&
                    !name.equals(barco.name) && !team.equals(barco.team) && barco.alive()) {
                try {
                    shoot(barco);
                } catch (RuntimeException | MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                Thread recargar = reload();
                recargar.start();
                try {
                    recargar.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public synchronized boolean alive(){
        return life > 0;
    }

    public synchronized double getX(){
        return x;
    }

    public synchronized double getY(){
        return y;
    }

    public synchronized void setX(double x){
        this.x = x;
    }

    public synchronized void setY(double y){
        this.y = y;
    }


    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                game.getVista().getChildren().addAll(vBox);
                moveShip();
                detect().start();
            }
        });
    }
}



