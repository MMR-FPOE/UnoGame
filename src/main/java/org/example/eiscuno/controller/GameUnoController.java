package org.example.eiscuno.controller;

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
import org.example.eiscuno.model.player.Player;
import org.example.eiscuno.model.table.Table;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.view.alert.AlertBox;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController {

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

    private String changeColor;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;

    private final EISCUnoEnum eiscUnoEnum = EISCUnoEnum.UNO;
    boolean humanTurn = true;
    boolean machineTurn = true;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize(){
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView, this.gameUno);
        threadPlayMachine.start();
        putFirstCard();
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

                    if (table.getCurrentCardOnTheTable().getValue().equals("WILD")) {
                        machineChooseColor();
                        System.out.println("Maquina cambia color");
                    } else if (card.getValue().equals("WILD")) {
                        changeColor();
                        isPlayable.set(true);
                        System.out.println("Cambio color");
                    } else if (card.getValue().startsWith("REVERSE")) {
                        machineTurn = false;
                        isPlayable.set(true);
                    } else if (checkColor(card)) {
                        if (card.getValue().startsWith("SKIP")){
                            System.out.println("XD"); // XD
                        }else{
                            isPlayable.set(true);
                        }
                    } else if (table.getCurrentCardOnTheTable().getValue().equals(card.getValue())) {
                        isPlayable.set(true);
                    }else{
                        isPlayable.set(true);
                    }
                    if (isPlayable.get() && humanTurn) {
                        humanTurn = false;
                        gameUno.playCard(card);
                        tableImageView.setImage(card.getImage());
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                        threadPlayMachine.setHasPlayerPlayed(machineTurn);
                        printCardsHumanPlayer();
                        machineTurn = true;
                        this.gameUno.validateSpecialCard(card, this.machinePlayer);
                    }
                });
                this.gridPaneCardsPlayer.add(cardImageView, i, 0);
            }
        setHumanTurn();
    }

    private void putFirstCard(){
        Card firstCard = deck.takeCard();
        if (!firstCard.getValue().equals("+4") &&
            !firstCard.getValue().startsWith("+2") && !firstCard.getValue().equals("WILD") &&
            !firstCard.getValue().startsWith("SKIP")) {
            gameUno.playCard(firstCard);
            tableImageView.setImage(firstCard.getImage());
        }else{
            putFirstCard();
        }
    }

    private boolean specialCases(){
        if (table.getCurrentCardOnTheTable().getValue().equals("FOUR_WILD_DRAW") || table.getCurrentCardOnTheTable().getValue().startsWith("TWO_WILD_DRAW_")) {
            System.out.println("+4 o +2 case");
            return true;
        }
        return false;
    }

    private String CardIdentifier(){
        if (table.getCurrentCardOnTheTable().getValue().equals("FOUR_WILD_DRAW")) {
            return "FOUR_WILD_DRAW";
        } else if (table.getCurrentCardOnTheTable().getValue().startsWith("TWO_WILD_DRAW_")) {
            return "TWO_WILD_DRAW_";
        }
        return "";
    }
    private void changeColor(){
        AlertBox alertBox = new AlertBox();
        alertBox.chooseColor("Cambio de Color", "¡Elige un color para el contrincante!", "");
        threadPlayMachine.setColor(alertBox.getColor());
    }

    private void machineChooseColor(){
        new AlertBox().machineChooseColor();
    }

    private void printMachineCards(){
        int length = machinePlayer.getArrayCardLength();
        machineCardsLength.setText("x" + length);
    }

    private boolean checkColor(Card card){
        return table.getCurrentCardOnTheTable().getColor().equals(card.getColor());
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

    public void setHumanTurn(){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                humanTurn = true;
                timer.cancel();
            }
        };
        timer.schedule(task, 2000);
        printMachineCards();
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
    }

    @FXML
    void closeGame() {
        System.exit(0);
    }
}
