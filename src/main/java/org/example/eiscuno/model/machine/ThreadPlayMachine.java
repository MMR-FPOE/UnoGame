package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.observer.ThreadObservable;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private String color;
    private volatile boolean hasPlayerPlayed;
    private volatile boolean humanHaveBeenBlocked;
    private GameUnoController unoController;
    private GameUno gameUno;
    ThreadObservable observable = new ThreadObservable();

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUnoController controller, GameUno gameUno) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.humanHaveBeenBlocked = false;
        this.gameUno = gameUno;
        this.unoController = controller;

        observable.addObserver(controller);
    }

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

    private void updateObservers(){
        observable.notifyObservers();
    }
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

    public void setColor(String color){
        this.color = color;
    }

    public String getColor(){ return color;}

    public void setHasPlayerPlayed(boolean hasPlayerPlayed) {
        this.hasPlayerPlayed = hasPlayerPlayed;
    }

    private Integer findPosCardsMachinePlayer(Card card) {
        for (int i = 0; i < this.machinePlayer.getCardsPlayer().size(); i++) {
            if (this.machinePlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

}
