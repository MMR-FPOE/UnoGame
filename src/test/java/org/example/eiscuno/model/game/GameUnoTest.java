package org.example.eiscuno.model.game;

import javafx.stage.Stage;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class GameUnoTest extends ApplicationTest {

    public void start(Stage stage) {
        // This is required to start the JavaFX application thread.
    }

    /**
     * Test if the isDoubleTurn method returns a true for a +4
     */
    @Test
    void isDoubleTurnWorksWell(){
        var humanPlayer = new Player("HUMAN_PLAYER");
        var machinePlayer = new Player("MACHINE_PLAYER");
        var deck = new Deck();
        var table = new Table();
        var gameUno = new GameUno(humanPlayer, machinePlayer, deck, table);

        boolean isWild = false;

        while (!isWild){
            var card = deck.takeCard();
            if(card.getValue().equals("+4")){
                table.addCardOnTheTable(card);
                isWild = gameUno.isDoubleTurn();
            }
        }
        assertTrue(gameUno.isDoubleTurn(), "Expected true");
        assertEquals("+4", table.getCurrentCardOnTheTable().getValue());
    }

    /**
     * Test if the cleanTableCards method, returns a cardsTable array a size of 1
     * The method adds the cards form the table (except the last one) to the deck
     */
    @Test
    void cleanTableCardsPassTheCardsToTheDeck(){
        var deck = new Deck();
        var table = new Table();

        var card = deck.takeCard();
        for(int i=0; i < 5; i++){
            table.addCardOnTheTable(card);
        }
        table.cleanTableCards(deck);

        assertEquals(1, table.getCardsTable().size());

    }
}