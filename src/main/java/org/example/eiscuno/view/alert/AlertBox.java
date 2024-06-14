package org.example.eiscuno.view.alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertBox implements IAlertBox{

    public void WinOrLose(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType restart = new ButtonType("Jugar de nuevo");
        ButtonType exit = new ButtonType("Salir");

        alert.getButtonTypes().setAll(restart, exit);

        ButtonType response = alert.showAndWait().orElse(ButtonType.CANCEL);

        /*
        if (response == restart) {
            try {
                WelcomeStage.getInstance();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            GameStage.deleteInstance();

        } else if (response == exit) {
            GameStage.deleteInstance();
        }*/
    }

    public void chooseColor(String title, String header, String content){

        String color = "";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType amarillo = new ButtonType("Amarillo");
        ButtonType rojo = new ButtonType("Rojo");
        ButtonType verde = new ButtonType("Verde");
        ButtonType azul = new ButtonType("Azul");

        alert.getButtonTypes().setAll(amarillo, rojo, verde, azul);

        ButtonType response = alert.showAndWait().orElse(ButtonType.CANCEL);

        if (response == amarillo){
            color = "Amarillo";
        } else if (response == rojo) {
            color = "Rojo";
        }else if (response == verde){
            color = "Verde";
        }else if (response == azul){
            color = "Azul";
        }

    }

}
