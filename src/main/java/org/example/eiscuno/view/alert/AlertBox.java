package org.example.eiscuno.view.alert;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertBox implements IAlertBox{
    String color;

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
    }

    public String getColor(){
        return color;
    }

}
