package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
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
    private volatile boolean haveBeenBlocked;
    private GameUno gameUno;

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUno gameUno) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.haveBeenBlocked = false;
        this.gameUno = gameUno;
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
                hasPlayerPlayed = false;
            }
        }
    }

    public void takeACard(Card card){
        table.addCardOnTheTable(card);
        machinePlayer.removeCard(findPosCardsMachinePlayer(card));
        tableImageView.setImage(card.getImage());
    }

    public boolean searchWild(){
        boolean isWild = false;
        for (Card wildCard : machinePlayer.getCardsPlayer()) {
            if (wildCard.getValue().equals("FOUR_WILD_DRAW") || wildCard.getValue().startsWith("TWO_WILD_DRAW_")) {
                takeACard(wildCard);
                isWild = true;
                break;

            }
        }
        return isWild;
    }
    private void putCardOnTheTable(){
        if(table.getCurrentCardOnTheTable().getColor() != "NON_COLOR"){
            color = table.getCurrentCardOnTheTable().getColor();
        }
        AtomicBoolean isPlayable = new AtomicBoolean(false);

        if(!machinePlayer.getCardsPlayer().isEmpty() && !machineHasBeenBlocked()) {

            while (!isPlayable.get()) {

                for(Card card: machinePlayer.getCardsPlayer()){

                    if (this.table.getCurrentCardOnTheTable().getColor().equals(card.getColor()) ||
                            this.table.getCurrentCardOnTheTable().getValue().equals(card.getValue())){
                            isPlayable.set(true);
                    } else {
                        if(card.getValue().equals("FOUR_WILD_DRAW") || card.getValue().startsWith("TWO_WILD_DRAW_")){
                            if(card.getValue().startsWith("TWO_WILD_DRAW_")){
                                if(checkColor(card)){
                                    isPlayable.set(true);
                                }
                            }
                        }
                    }
                    if ((machinePlayer.getCardsPlayer().indexOf(card) == machinePlayer.getCardsPlayer().size() - 1) && !isPlayable.get()){
                        System.out.println("no cards available");
                        gameUno.eatCard(machinePlayer, 4);
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
            }
        }else{
            //machine has blocked or has no cards
        }
    }

    private boolean checkColor(Card card){
        return color.equals(card.getColor());
    }

    public void setColor(String color){
        this.color = color;
    }

    private boolean machineHasBeenBlocked(){
        if (table.getCurrentCardOnTheTable().getValue().startsWith("SKIP_")) {
            this.hasPlayerPlayed = false;
            return true;
        }
        return false;
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
