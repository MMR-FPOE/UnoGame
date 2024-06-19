package org.example.eiscuno.model.machine;

import javafx.scene.image.ImageView;
import org.example.eiscuno.controller.GameUnoController;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.observer.ThreadObservable;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadPlayMachine extends Thread {
    private Table table;
    private Player machinePlayer;
    private ImageView tableImageView;
    private String color;
    private volatile boolean hasPlayerPlayed;
    private volatile boolean haveBeenBlocked;
    private volatile boolean isPaused;

    private volatile boolean hasToEat;
    AtomicBoolean noCard = new AtomicBoolean(false);
    ThreadObservable observable = new ThreadObservable();

    public ThreadPlayMachine(Table table, Player machinePlayer, ImageView tableImageView, GameUnoController controller) {
        this.table = table;
        this.machinePlayer = machinePlayer;
        this.tableImageView = tableImageView;
        this.hasPlayerPlayed = false;
        this.haveBeenBlocked = false;

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
                hasPlayerPlayed = false;
                updateObservers();
            }
        }
    }

    private void updateObservers(){
        observable.notifyObservers();
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
        if(table.getCurrentCardOnTheTable().getColor() != null){
            color = table.getCurrentCardOnTheTable().getColor();
        }
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
                        // Comer cartas hasta que exista carta disponible
                    }
                    if (isPlayable.get()) {
                        table.addCardOnTheTable(card);
                        machinePlayer.removeCard(findPosCardsMachinePlayer(card));
                        tableImageView.setImage(card.getImage());
                        break;
                    }
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
        return color.equals(card.getColor());
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setColor(String color){
        this.color = color;
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
