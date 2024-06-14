package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private volatile boolean hasPlayerPlayed;
    private volatile boolean haveBeenBlocked;
    private volatile boolean isPaused;


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
            if (wildCard.getValue().equals("FOUR_WILD_DRAW") || wildCard    .getValue().startsWith("TWO_WILD_DRAW_")) {
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
                int index = (int) (Math.random() * machinePlayer.getCardsPlayer().size());
                Card card = machinePlayer.getCard(index);

                if(specialCases()){

                } else if (checkColor(card)) {
                    System.out.println("nef");
                    isPlayable.set(true);
                    System.out.println("color, maquina");
                } else if (table.getCurrentCardOnTheTable().getValue().equals(card.getValue())) {
                    isPlayable.set(true);
                } else {
                    if (card.getValue().equals("FOUR_WILD_DRAW") || card.getValue().startsWith("TWO_WILD_DRAW_")) {
                        isPlayable.set(true);
                    }
                }
                if (isPlayable.get()) {
                    table.addCardOnTheTable(card);
                    machinePlayer.removeCard(findPosCardsMachinePlayer(card));
                    tableImageView.setImage(card.getImage());
                }
            }}else{
            //machine has blocked or has no cards
        }

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
            System.out.println("+4 o +2 case");
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
