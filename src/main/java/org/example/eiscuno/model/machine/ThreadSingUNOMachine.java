package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.view.alert.AlertBox;

/**
 * Machine thread to sing one
 */
public class ThreadSingUNOMachine implements Runnable{
    private Player humanPlayer;
    private Player machinePlayer;
    private GameUno gameUno;

    /**
     * Constructs a new ThreadSIng instance.
     *
     * @param humanPlayer   The human player participating in the game.
     * @param machinePlayer The machine player participating in the game.
     * @param gameUno        The game uno instance for the game.
     */
    public ThreadSingUNOMachine(Player humanPlayer, Player machinePlayer,  GameUno gameUno){
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.gameUno = gameUno;
    }

    /**
     * Thread running method
     */
    @Override
    public void run(){
        while (true){
            try {
                Thread.sleep((long) (Math.random() * 5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hasOneCardTheMachinePlayer();
            hasOneCardTheHumanPlayer();
        }
    }

    /**
     * checks if the Human player has only one card left
     */
    private void hasOneCardTheHumanPlayer(){
        if(humanPlayer.getCardsPlayer().size() == 1 && !humanPlayer.getProtectedByUno()){
            Platform.runLater(this::alertForHumanByMachine);
            gameUno.haveSungOne("MACHINE_PLAYER");
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * checks if the Machine player has only one card left
     */
    private void hasOneCardTheMachinePlayer(){
        if(machinePlayer.getCardsPlayer().size() == 1 && !machinePlayer.getProtectedByUno()){
            Platform.runLater(this::alertMachine);
            machinePlayer.setProtectedByUno(true);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the sing one alert
     */
    private void alertMachine(){
        new AlertBox().SingsUno("¡Uno!", "La máquina se protege");
    }
    private void alertForHumanByMachine(){ new AlertBox().SingsUno("¡Uno!", "La máquina cantó uno por ti, comes una carta");}

}
