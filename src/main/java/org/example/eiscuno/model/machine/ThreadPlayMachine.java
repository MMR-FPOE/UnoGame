package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.observer.ThreadObservable;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread for the machine game
 */
public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile String color;
    private volatile boolean hasPlayerPlayed;
    private GameUno gameUno;
    ThreadObservable observable = new ThreadObservable();

    /**
     * Constructs a new ThreadPlay instance.
     *
     * @param table         The table where cards are placed during the game.
     * @param machinePlayer The machine player participating in the game.
     * @param tableImageView   The imageview for the image of the card.
     * @param controller        The controller instance.
     * @param gameUno          The game uno instance for the game.
     */
    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUnoController controller, GameUno gameUno) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.gameUno = gameUno;

        observable.addObserver(controller);
    }

    /**
     * Thread running method
     */
    public void run() {
        while (true){
            if(hasPlayerPlayed){
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                putCardOnTheTable();
                hasPlayerPlayed = gameUno.isDoubleTurn();
                if(!gameUno.isDoubleTurn()) {
                    updateObservers();
                }
            }
        }
    }

    /**
     * Updates the Observers (part of the observer pattern)
     */
    private void updateObservers(){
        observable.notifyObservers();
    }

    /**
     * Put the card on the table
     * Checks according to the rules of the game
     */
    private void putCardOnTheTable(){
        if(table.getCurrentCardOnTheTable().getColor() != "NON_COLOR"){
            color = table.getCurrentCardOnTheTable().getColor();
        }
        AtomicBoolean isPlayable = new AtomicBoolean(false);

        if(!machinePlayer.getCardsPlayer().isEmpty()){
            for(Card card: machinePlayer.getCardsPlayer()){
                if (this.color.equals(card.getColor()) ||
                        this.table.getCurrentCardOnTheTable().getValue().equals(card.getValue())){
                    isPlayable.set(true);
                }else if (card.getValue().equals("+4") || card.getValue().equals("WILD")){
                    isPlayable.set(true);
                }else if (card.getValue().equals("+2") || card.getValue().equals("REVERSE")){
                    if (this.color.equals(card.getColor()))
                        isPlayable.set(true);
                }
                if ((machinePlayer.getCardsPlayer().indexOf(card) == machinePlayer.getCardsPlayer().size() - 1) && !isPlayable.get()){
                    System.out.println("no cards available");
                    gameUno.eatCard(machinePlayer, 1);
                    putCardOnTheTable();
                    break;
                }
                if (isPlayable.get()) {
                    table.addCardOnTheTable(card);
                    machinePlayer.removeCard(findPosCardsMachinePlayer(card));
                    tableImageView.setImage(card.getImage());
                    gameUno.validateSpecialCard(card, gameUno.getHumanPlayer());
                    break;
                }
            }
        }
    }

    /**
     * Sets the color of the game in the thread
     */
    public void setColor(String color){
        this.color = color;
    }

    /**
     * Obtains the color of the game fron the controller
     */
    public String getColor(){ return color;}

    /**
     * Sets the hasPlayerPlayed variable
     */
    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    /**
     * Obtains the position of the card in the machine's array
     */
    private Integer findPosCardsMachinePlayer(Card card) {
        for (int i = 0; i < this.machinePlayer.getCardsPlayer().size(); i++) {
            if (this.machinePlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

}
