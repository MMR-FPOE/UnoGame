package org.example.eiscuno.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.example.eiscuno.model.card.Card;
import org.example.eiscuno.model.deck.Deck;
import org.example.eiscuno.model.game.GameUno;
import org.example.eiscuno.model.machine.ThreadPlayMachine;
import org.example.eiscuno.model.machine.ThreadSingUNOMachine;
import org.example.eiscuno.model.observer.Observer;
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.view.alert.AlertBox;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController implements Observer {

    @FXML
    private Label machineCardsLength;

    @FXML
    private GridPane gridPaneCardsPlayer;

    @FXML
    private ImageView tableImageView;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;
    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;
    volatile String color;
    boolean humanTurn = true;
    boolean machineTurn;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize(){
        initVariables();
        putFirstCard();
        this.gameUno.startGame();
        printCardsHumanPlayer();

        color = table.getCurrentCardOnTheTable().getColor();

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer, this.machinePlayer, this.gameUno);
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this, this.gameUno);
        threadPlayMachine.start();

    }

    /**
     * Initializes the variables for the game.
     */
    private void initVariables() {
        this.humanPlayer = new Player("HUMAN_PLAYER");
        this.machinePlayer = new Player("MACHINE_PLAYER");
        this.deck = new Deck();
        this.table = new Table();
        this.gameUno = new GameUno(this.humanPlayer, this.machinePlayer, this.deck, this.table);
        this.posInitCardToShow = 0;
    }

    /**
     * Prints the human player's cards on the grid pane.
     */
    private void printCardsHumanPlayer() {
        AtomicBoolean isPlayable = new AtomicBoolean(false);
        this.gridPaneCardsPlayer.getChildren().clear();
        Card[] currentVisibleCardsHumanPlayer = this.gameUno.getCurrentVisibleCardsHumanPlayer(this.posInitCardToShow);

            for (int i = 0; i < currentVisibleCardsHumanPlayer.length; i++) {
                Card card = currentVisibleCardsHumanPlayer[i];
                ImageView cardImageView = card.getCard();

                cardImageView.setOnMouseClicked((MouseEvent event) -> {
                    machineTurn = true;
                    getColor();

                    if (card.getValue().equals("WILD")) {
                        changeColor("¡Elige un color para el contrincante!");
                        isPlayable.set(true);
                    } else if (checkColor(card) || card.getValue().equals(table.getCurrentCardOnTheTable().getValue())) {
                        if (card.getValue().equals("SKIP") || card.getValue().equals("+2") || card.getValue().equals("REVERSE")){
                            machineTurn = false;
                            isPlayable.set(true);
                        }else{
                            isPlayable.set(true);
                        }
                    }else if (card.getValue().equals("+4")){
                        // machineTurn = false;
                         changeColor("¡Haz puesto un +4, cambia tu color de juego!");
                         isPlayable.set(true);
                    }
                    if (isPlayable.get() && humanTurn) {
                        if(machineTurn) { humanTurn = false; }
                        gameUno.playCard(card);
                        tableImageView.setImage(card.getImage());
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                        gameUno.validateSpecialCard(card, this.machinePlayer);
                        threadPlayMachine.setHasPlayerPlayed(machineTurn);
                        printCardsHumanPlayer();
                    }
                });
                this.gridPaneCardsPlayer.add(cardImageView, i, 0);
            }
    }

    /**
     * puts the first card, verifying that is not a wild.
     */
    private void putFirstCard(){
        Card firstCard = deck.takeCard();
        if (!firstCard.getValue().equals("+4") &&
            !firstCard.getValue().startsWith("+2") && !firstCard.getValue().equals("WILD") &&
            !firstCard.getValue().startsWith("SKIP") && !firstCard.getValue().startsWith("REVERSE")) {
            gameUno.playCard(firstCard);
            tableImageView.setImage(firstCard.getImage());
        }else{
            putFirstCard();
        }
    }

    /**
     * Shows the human's alert, for color change
     * @param header        header message
     */
    private void changeColor(String header){
        AlertBox alertBox = new AlertBox();
        alertBox.chooseColor("Cambio de Color", header);
        threadPlayMachine.setColor(alertBox.getColor());
    }


    /**
     * Updates the label of the number of cards in the machine
     */
    private void printMachineCards(){
        int length = machinePlayer.getArrayCardLength();
        machineCardsLength.setText("x" + length);
    }

    /**
     * Shows the machine's alert, for color change
     */
    public void machineChooseColor(){
        System.out.println("color: " + color);
        if(this.table.getCurrentCardOnTheTable().getValue().equals("WILD") || this.table.getCurrentCardOnTheTable().getValue().equals("+4")) {
            AlertBox alertBox = new AlertBox();
            alertBox.machineChooseColor();
            threadPlayMachine.setColor(alertBox.getColor());
            System.out.println("color: " + color);
        }
    }

    /**
     * Set de color of the game
     */
    private void getColor(){
        if (table.getCurrentCardOnTheTable().getColor().equals("NON_COLOR")){
            color = threadPlayMachine.getColor();
        }else{
            color = table.getCurrentCardOnTheTable().getColor();
        }
    }

    /**
     * checks if the color is the same as the color of the card to be played
     */
    private boolean checkColor(Card card){
        return color.equals(card.getColor());
    }

    /**
     * Finds the position of a specific card in the human player's hand.
     *
     * @param card the card to find
     * @return the position of the card, or -1 if not found
     */
    private Integer findPosCardsHumanPlayer(Card card) {
        for (int i = 0; i < this.humanPlayer.getCardsPlayer().size(); i++) {
            if (this.humanPlayer.getCardsPlayer().get(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Handles the "Back" button action to show the previous set of cards.
     *
     */
    @FXML
    void onHandleBack() {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the "Next" button action to show the next set of cards.
     *
     */
    @FXML
    void onHandleNext() {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 7) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the action of taking a card.
     *
     */
    @FXML
    void onHandleTakeCard() {
        Card card = deck.takeCard();
        humanPlayer.addCard(card);
        printCardsHumanPlayer();
    }

    /**
     * Handles the action of saying "Uno".
     *
     */
    @FXML
    void onHandleUno() {
        // Implement logic to handle Uno event here
        if(humanPlayer.getCardsPlayer().size() == 1){
            humanPlayer.setProtectedByUno(true);
            new AlertBox().SingsUno("¡Cantaste Uno!", "Estás protegido");
        }
        if(!machinePlayer.isProtectedByUno() && machinePlayer.getCardsPlayer().size() == 1){
            new AlertBox().SingsUno("¡Cantaste Uno!", "La máquina come una carta");
            gameUno.haveSungOne("HUMAN_PLAYER");
            System.out.println("machine eats one card");
        }
    }

    /**
     * Closes the game
     *
     */
    @FXML
    void closeGame() {
        System.exit(0);
    }

    /**
     * Updates the game (part of the observer pattern)
     *
     */
    @Override
    public void update() {
        Platform.runLater(this::machineChooseColor);
        humanTurn = true;
        machineTurn = false;
        Platform.runLater(this::printMachineCards);
    }
}
