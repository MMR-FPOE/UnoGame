package org.example.eiscuno.model.game;

import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a game of Uno.
 * This class manages the game logic and interactions between players, deck, and the table.
 */
public class GameUno implements IGameUno {

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;

    /**
     * Constructs a new GameUno instance.
     *
     * @param humanPlayer   The human player participating in the game.
     * @param machinePlayer The machine player participating in the game.
     * @param deck          The deck of cards used in the game.
     * @param table         The table where cards are placed during the game.
     */
    public GameUno(Player humanPlayer, Player machinePlayer, Deck deck, Table table) {
        this.humanPlayer = humanPlayer;
        this.machinePlayer = machinePlayer;
        this.deck = deck;
        this.table = table;
    }

    /**
     * Starts the Uno game by distributing cards to players.
     * The human player and the machine player each receive 10 cards from the deck.
     */
    @Override
    public void startGame() {
        for (int i = 0; i < 14; i++) {
            if (i < 7) {
                humanPlayer.addCard(this.deck.takeCard());
            } else {
                machinePlayer.addCard(this.deck.takeCard());
            }
        }
    }

    /**
     * Allows a player to draw a specified number of cards from the deck.
     *
     * @param player        The player who will draw cards.
     * @param numberOfCards The number of cards to draw.
     */
    @Override
    public void eatCard(Player player, int numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            player.addCard(this.deck.takeCard());
        }
        if(deck.isEmpty()){
            System.out.println("MAZO VACIO");
            table.cleanTableCards(deck);
        }
    }

    /**
     * Places a card on the table during the game.
     *
     * @param card The card to be placed on the table.
     */
    @Override
    public void playCard(Card card) {
        this.table.addCardOnTheTable(card);
    }

    /**
     * Handles the scenario when a player shouts "Uno", forcing the other player to draw a card.
     *
     * @param playerWhoSang The player who shouted "Uno".
     */
    @Override
    public void haveSungOne(String playerWhoSang) {
        if (playerWhoSang.equals("HUMAN_PLAYER")) {
            machinePlayer.addCard(this.deck.takeCard());
        } else {
            humanPlayer.addCard(this.deck.takeCard());
        }
    }

    /**
     * Retrieves the current visible cards of the human player starting from a specific position.
     *
     * @param posInitCardToShow The initial position of the cards to show.
     * @return An array of cards visible to the human player.
     */
    @Override
    public Card[] getCurrentVisibleCardsHumanPlayer(int posInitCardToShow) {
        int totalCards = this.humanPlayer.getCardsPlayer().size();
        int numVisibleCards = Math.min(7, totalCards - posInitCardToShow);
        Card[] cards = new Card[numVisibleCards];

        for (int i = 0; i < numVisibleCards; i++) {
            cards[i] = this.humanPlayer.getCard(posInitCardToShow + i);
        }

        return cards;
    }

    /**
     * Checks if the game is over.
     *
     * @return True if the deck is empty, indicating the game is over; otherwise, false.
     */
    @Override
    public Boolean isGameOver() {
        return null;
    }

    @Override
    public void validateSpecialCard(Card card, Player player) {
        int numberOfCards = 0;

        if(card.getValue().contains("+2")) {
            numberOfCards = 2;
        } else if (card.getValue().contains("+4")) {
            numberOfCards = 4;
        }

        if(numberOfCards > 0){
            System.out.println(player.getTypePlayer() + " have: " + player.getCardsPlayer().size() + " cards");
        }

        if(numberOfCards > deck.deckLength()){
            table.cleanTableCards(deck);
        }

        for (int i = 0; i < numberOfCards; i++) {
            player.addCard(this.deck.takeCard());
        }

        if(numberOfCards > 0){
            System.out.println(player.getTypePlayer() + " eat now: " + numberOfCards + " cards");
            System.out.println(player.getTypePlayer() + " have now: " + player.getCardsPlayer().size() + " cards");
        }
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public boolean isDoubleTurn(){
        if (this.table.getCurrentCardOnTheTable().getValue().equals("REVERSE") ||
                this.table.getCurrentCardOnTheTable().getValue().equals("SKIP") ||
                this.table.getCurrentCardOnTheTable().getValue().equals("+2") ||
                this.table.getCurrentCardOnTheTable().getValue().equals("+4")){
            return true;
        }else{
            return false;
        }
    }
}
