package org.example.eiscuno.model.table;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;

import java.util.ArrayList;

/**
 * Represents the table in the Uno game where cards are played.
 */
public class Table {
    private ArrayList<Card> cardsTable;

    /**
     * Constructs a new Table object with no cards on it.
     */
    public Table(){
        this.cardsTable = new ArrayList<Card>();
    }

    /**
     * Adds a card to the table.
     *
     * @param card The card to be added to the table.
     */
    public void addCardOnTheTable(Card card){
        this.cardsTable.add(card);
    }

    /**
     * Retrieves the current card on the table.
     *
     * @return The card currently on the table.
     */
    public Card getCurrentCardOnTheTable() throws IndexOutOfBoundsException {
        return this.cardsTable.get(this.cardsTable.size()-1);
    }

    public void cleanTableCards(Deck deck){
        for (Card card : cardsTable){
            if(cardsTable.size() != 1){
            deck.addCard(card);
            }
        }
        while(cardsTable.size() != 1){
            cardsTable.remove(0);
        }
    }
}
