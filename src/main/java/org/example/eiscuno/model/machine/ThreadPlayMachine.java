package org.example.eiscuno.model.machine;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.alert.AlertBox;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;
    private volatile boolean haveBeenBlocked;
    private volatile boolean isPaused;

    private volatile boolean hasToEat;
    AtomicBoolean noCard = new AtomicBoolean(false);


    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.haveBeenBlocked = false;
    }

    public void run() {
        while (true){
            if(hasPlayerPlayed){
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Aqui iria la logica de colocar la carta
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
        AtomicBoolean isPlayable = new AtomicBoolean(false);

        if(!machinePlayer.getCardsPlayer().isEmpty() && !machineHasBeenBlocked()) {

            while (!isPlayable.get()) {

                for(Card card: machinePlayer.getCardsPlayer()){
                    if(specialCases()){
                        if(!searchWild()){
                            hasToEat = true;
                            break;
                        }
                    }
                    if (checkColor(card)) {
                        isPlayable.set(true);
                    } else if (table.getCurrentCardOnTheTable().getValue().equals(card.getValue())) {
                        isPlayable.set(true);
                    } else if (table.getCurrentCardOnTheTable().getValue().equals("WILD")){

                    } else {
                        if (card.getValue().equals("FOUR_WILD_DRAW") || card.getValue().startsWith("TWO_WILD_DRAW_")){
                            if(card.getValue().startsWith("TWO_WILD_DRAW_")){
                                if(checkColor(card)){
                                    isPlayable.set(true);
                                }
                            }else{
                                isPlayable.set(true);
                            }
                        }
                    }
                    if ((machinePlayer.getCardsPlayer().indexOf(card) == machinePlayer.getCardsPlayer().size() - 1) && !isPlayable.get()){
                        noCard.set(true);
                        System.out.println("no cards available");
                        break;
                    }
                    if (isPlayable.get()) {
                        table.addCardOnTheTable(card);
                        machinePlayer.removeCard(findPosCardsMachinePlayer(card));
                        tableImageView.setImage(card.getImage());
                        break;
                    }
                }
                    if (noCard.get()){
                        machineTakeCard();
                    }
                    if (noCard.get()){
                        break;
                    }

            }
        }else{
            //machine has blocked or has no cards
        }

    }

    public void setNoCard(boolean cardState) {
        this.noCard.set(cardState);
    }

    public boolean getHasToEat(){
        return this.hasToEat;
    }
    public AtomicBoolean machineTakeCard(){
        return noCard;
    }
    public void pauseThread() {
        try {
            while(isPaused) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    synchronized public void reload()
    {
        System.out.println("the thread using notify()");
        notify();
    }

    private boolean checkColor(Card card){
        try {
            if (table.getCurrentCardOnTheTable().getColor().equals(card.getColor())) {
                return true;
            }
        }catch (NullPointerException e){
            e.getMessage();
        }
        return false;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    private boolean specialCases(){
        if (table.getCurrentCardOnTheTable().getValue().equals("FOUR_WILD_DRAW") || table.getCurrentCardOnTheTable().getValue().startsWith("TWO_WILD_DRAW_")) {
            return true;
        }
        return false;
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

    public void setHaveBeenBlocked(boolean haveBeenBlocked) {
        this.haveBeenBlocked = haveBeenBlocked;
    }

    public boolean getHasPlayerPlayed(){
        return this.hasPlayerPlayed;
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
