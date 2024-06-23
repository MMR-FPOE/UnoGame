package org.example.eiscuno.view.alert;

import javafx.scene.control.Alert;

public interface IAlertBox {
    void WinOrLose(String title, String header, String content);

    void chooseColor(String header);

    void machineChooseColor();

    void SingsUno(String header, String content);

    String getColor();

    void alertDelay(Alert alert);

}
