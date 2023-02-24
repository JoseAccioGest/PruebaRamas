package com.example.battleship;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.Random;

 class Disparo extends Thread{
     private Ship objetive;
     private Ship from;
     BattleShip game;
     ImageView image;
     private TranslateTransition transition;
    double xFrom;
    double yFrom;
    double xTo;
    double yTo;
    final String IMG_PATH = "bala.png";
    public Disparo(BattleShip game, Ship from, Ship objetivo) {
        this.game = game;
        this.xFrom = from.getX() + from.vBox.getBoundsInLocal().getCenterX();
        this.yFrom = from.getY() + from.vBox.getBoundsInLocal().getCenterY();
        this.xTo = objetivo.getX() + objetivo.vBox.getBoundsInLocal().getCenterX();
        this.yTo = objetivo.getY() + objetivo.vBox.getBoundsInLocal().getCenterY();
        this.objetive = objetivo;
        this.from = from;
        Image image = new Image(getClass().getResourceAsStream(IMG_PATH));
        this.image = new ImageView(image);
        this.image.setFitWidth(15);
        this.image.setPreserveRatio(true);
        this.image.setTranslateX(xFrom);
        this.image.setTranslateY(yFrom);
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                game.getVista().getChildren().addAll(image);
                moveLabel();
            }
        });
    }

    private void moveLabel() {
        transition = new TranslateTransition(Duration.millis(450), image);
        transition.setByX(xTo - xFrom);
        transition.setByY(yTo - yFrom);
        transition.setInterpolator(Interpolator.EASE_IN);
        transition.setAutoReverse(false);
        transition.play();
        transition.setOnFinished(event -> {
            game.getVista().getChildren().remove(image);
            double tramoA = 25 * from.precision/100;
            double tramoB = 50 * from.precision/100;
            double tramoC = 75 * from.precision/100;
            int randDisparo = getRandomNumber(1,100);
            if(randDisparo < tramoA){
                objetive.getShoot(from.damage);
            }else if(randDisparo < tramoB){
                objetive.getShoot(from.damage *0.5);
            }else if(randDisparo < tramoC) {
                objetive.getShoot(from.damage *0.25);
            }
        });
    }

    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

}
class Acorazado extends Ship{
    public Acorazado(BattleShip juego, String nombre, Teams team, double x, double y) {
        super(juego, nombre, team,"acorazado.png" ,170, 4, x, y,
                1000, 140, 100,110,1800);
    }
}

 class Destructor extends Ship{
    public Destructor(BattleShip juego, String nombre, Teams equipo, double x, double y) {
        super(juego, nombre, equipo,"destructor.png" ,140, 5, x, y,
                300, 100, 130,90,1400);
    }
}

 class Lancha extends Ship{
    public Lancha(BattleShip juego, String nombre, Teams equipo, double x, double y) {
        super(juego, nombre, equipo,"lancha.png" ,70, 8, x, y,
                110, 40, 90,60,400);
    }
}

 class Submarino extends Ship{
    public Submarino(BattleShip juego, String nombre, Teams equipo, double x, double y) {
        super(juego, nombre, equipo,"submarino.png" ,500, 2, x, y,
                250, 250, 110,110,4050);
    }
}


