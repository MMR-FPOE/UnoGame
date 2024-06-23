package org.example.eiscuno.view.alert;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

/**
 * Interface for creating alerts
 * Provides different methods to create alerts for the UnoGame
 */
public class AlertBox implements IAlertBox{
    volatile String color;

    /**
     * Adds a card to the player's hand.
     *
     * @param title The alert title
     * @param header The alert header.
     * @param content The alert content.
     */
    public void WinOrLose(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType exit = new ButtonType("Salir");

        alert.getButtonTypes().setAll(exit);

        ButtonType response = alert.showAndWait().orElse(ButtonType.CANCEL);

        if(response == exit){
            System.exit(0);
        }

    }

    /**
     * Create the human alert for the chooseColor in the game
     *
     * @param title The alert title
     * @param header The alert header.
     */
    public void chooseColor(String title, String header){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);

        ButtonType amarillo = new ButtonType("Amarillo");
        ButtonType rojo = new ButtonType("Rojo");
        ButtonType verde = new ButtonType("Verde");
        ButtonType azul = new ButtonType("Azul");

        alert.getButtonTypes().setAll(amarillo, rojo, verde, azul);

        ButtonType response = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (response == amarillo){
            color = "YELLOW";
        } else if (response == rojo) {
            color = "RED";
        }else if (response == verde){
            color = "GREEN";
        }else if (response == azul){
            color = "BLUE";
        }
    }

    /**
     * Creates the machine alert for the chooseColor in the game
     */
    public void machineChooseColor(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cambio de Color");
        alert.setHeaderText("La máquina eligió el color..");

        int index = (int) (Math.random() * 4);
        switch (index){
            case 0:
                alert.setContentText("YELLOW");
                color = "YELLOW";
                break;
            case 1:
                alert.setContentText("RED");
                color = "RED";
                break;
            case 2:
                alert.setContentText("BLUE");
                color = "BLUE";
                break;
            case 3:
                alert.setContentText("GREEN");
                color = "GREEN";
                break;
        }
        alert.show();
        Platform.runLater(() -> alertDelay(alert));
    }

    /**
     * Creates the alerts to sing one
     *
     * @param header The alert header.
     * @param content The alert content.
     */
    public void SingsUno(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Sang Uno!");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Gets the color variable
     *
     * @return color The color variable
     */
    public String getColor(){
        return color;
    }

    /**
     * Alerts delay
     */
    public void alertDelay(Alert alert){
        alert.show();

        PauseTransition delay = new PauseTransition(Duration.millis(2000));
        delay.setOnFinished(event -> alert.close());

        delay.play();
    }
}
