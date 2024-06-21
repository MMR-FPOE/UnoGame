package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.observer.ThreadObservable;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.alert.AlertBox;

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
        boolean noCardsAvailable = false;

        if(!machinePlayer.getCardsPlayer().isEmpty()){

            while (!isPlayable.get()) {

                for(Card card: machinePlayer.getCardsPlayer()){
                   // System.out.println("color: " + card.getColor() + " value: " + card.getValue());

                    if (this.table.getCurrentCardOnTheTable().getColor().equals(card.getColor()) ||
                        this.table.getCurrentCardOnTheTable().getValue().equals(card.getValue())){
                        isPlayable.set(true);
                    }else if (card.getValue().equals("+4") || card.getValue().equals("+2") || card.getValue().equals("WILD")
                            || card.getValue().equals("REVERSE")){
                        humanHaveBeenBlocked = true;
                        if (card.getValue().equals("+2") || card.getValue().equals("REVERSE")) {
                            if (this.table.getCurrentCardOnTheTable().getColor().equals(card.getColor())) {
                                isPlayable.set(true);
                            }
                        }else{
                            isPlayable.set(true);
                        }
                    }
                    if ((machinePlayer.getCardsPlayer().indexOf(card) == machinePlayer.getCardsPlayer().size() - 1) && !isPlayable.get()){
                        System.out.println("the last card: " + this.table.getCurrentCardOnTheTable().getColor() + " " +  this.table.getCurrentCardOnTheTable().getValue());
                        System.out.println("no cards available");
                        gameUno.eatCard(machinePlayer, 1);
                        noCardsAvailable = true;
                        putCardOnTheTable();
                        break;
                        // Comer cartas hasta que exista carta disponible
                    }
                    if (isPlayable.get()) {
                        table.addCardOnTheTable(card);
                        machinePlayer.removeCard(findPosCardsMachinePlayer(card));
                        tableImageView.setImage(card.getImage());
                        this.gameUno.validateSpecialCard(card, gameUno.getHumanPlayer());
                        break;
                    }
                }
                if(noCardsAvailable){
                    break;
                }
            }
        }else{
            //machine has blocked or has no cards
        }
    }

    public void setColor(String color){
        this.color = color;
    }

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
