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

    public boolean chooseColor(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        return true;
    }

}
