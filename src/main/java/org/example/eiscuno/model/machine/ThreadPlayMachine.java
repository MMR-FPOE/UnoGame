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
            }
        }
    }

    public void takeACard(Card card){
        table.addCardOnTheTable(card);
        machinePlayer.removeCard(findPosCardsMachinePlayer(card));
        tableImageView.setImage(card.getImage());
    }
    private void putCardOnTheTable(){

        if(!machinePlayer.getCardsPlayer().isEmpty() && !machineHasBeenBlocked()) {
            int index = (int) (Math.random() * machinePlayer.getCardsPlayer().size());
            Card card = machinePlayer.getCard(index);

            table.addCardOnTheTable(card);
            machinePlayer.removeCard(findPosCardsMachinePlayer(card));
            tableImageView.setImage(card.getImage());
        }
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

    private boolean especialCases(){
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

    private Integer findPosCardsMachinePlayer(Card card) {
        for (int i = 0; i < this.machinePlayer.getCardsPlayer().size(); i++) {
            if (this.machinePlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

}
