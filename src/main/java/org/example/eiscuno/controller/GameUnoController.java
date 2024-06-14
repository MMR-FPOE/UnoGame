package org.example.eiscuno.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
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
import org.example.eiscuno.view.GameUnoStage;
import org.example.eiscuno.view.alert.AlertBox;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller class for the Uno game.
 */
public class GameUnoController {

    @FXML
    private GridPane gridPaneCardsMachine;

    @FXML
    private GridPane gridPaneCardsPlayer;

    @FXML
    private ImageView tableImageView;

    @FXML
    private ImageView unoImageView;

    private Player humanPlayer;
    private Player machinePlayer;
    private Deck deck;
    private Table table;
    private GameUno gameUno;
    private int posInitCardToShow;

    private ThreadSingUNOMachine threadSingUNOMachine;
    private ThreadPlayMachine threadPlayMachine;

    private final EISCUnoEnum eiscUnoEnum = EISCUnoEnum.UNO;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        initVariables();
        this.gameUno.startGame();
        printCardsHumanPlayer();

        threadSingUNOMachine = new ThreadSingUNOMachine(this.humanPlayer.getCardsPlayer());
        Thread t = new Thread(threadSingUNOMachine, "ThreadSingUNO");
        t.start();

        threadPlayMachine = new ThreadPlayMachine(this.table, this.machinePlayer, this.tableImageView);
        threadPlayMachine.start();

        unoImageView.setImage(new Image(String.valueOf(getClass().getResource(eiscUnoEnum.getFilePath()))));
        unoImageView.setFitWidth(80);
        unoImageView.setFitHeight(80);
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
                    // Aqui deberian verificar si pueden en la tabla jugar esa carta
                    if (tableImageView.getImage() != null && !humanHasBeenBlocked()) {
                        if (specialCases()) {
                            if (table.getCurrentCardOnTheTable().getValue().equals(card.getValue())) {
                                isPlayable.set(true);
                            } else if (card.getValue().equals("FOUR_WILD_DRAW")) {
                                isPlayable.set(true);
                            }
                        } else if (card.getValue().equals("WILD")) {
                            changeColor();
                            isPlayable.set(true);
                        } else if (checkColor(card)) {
                            if (card.getValue().startsWith("SKIP_")){
                                machineBlocked();
                            }else{
                                isPlayable.set(true);
                            }
                        } else if (table.getCurrentCardOnTheTable().getValue().equals(card.getValue())) {
                            isPlayable.set(true);
                        }else if (card.getValue().equals("FOUR_WILD_DRAW") || card.getValue().startsWith("TWO_WILD_DRAW_")) {
                            if (card.getValue().equals("TWO_WILD_DRAW_")) {
                                if (checkColor(card)){
                                    isPlayable.set(true);
                                }
                            } else if (!Objects.equals(card.getValue(), "TWO_WILD_DRAW_")) {
                                isPlayable.set(true);
                            }
                        }
                    }
                    if (isPlayable.get() || tableImageView.getImage() == null) {
                        gameUno.playCard(card);
                        tableImageView.setImage(card.getImage());
                        humanPlayer.removeCard(findPosCardsHumanPlayer(card));
                        threadPlayMachine.setHasPlayerPlayed(true);
                        printCardsHumanPlayer();
                    }
                });
                this.gridPaneCardsPlayer.add(cardImageView, i, 0);

                }
    }
    private boolean humanHasBeenBlocked(){

        if (tableImageView.getImage() != null){
            if (table.getCurrentCardOnTheTable().getValue().startsWith("SKIP_")) {
                threadPlayMachine.setHasPlayerPlayed(true);
                //cede el turno a la máquina
                System.out.println("human blocked");

                return true;
            }
        }
        return false;
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

    private void machineHasToEat(){
        int numberOfCards = 0;
        if (specialCases()){
            if (!threadPlayMachine.searchWild()){
                if (CardIdentifier().equals("FOUR_WILD_DRAW")) {
                    gameUno.eatCard(machinePlayer, 4);
                }else{
                    gameUno.eatCard(machinePlayer, 2);
                }
            }
        }
    }

    private void machineBlocked(){
        //threadPlayMachine.setHasPlayerPlayed(false);
        threadPlayMachine.setPaused(true);
        threadPlayMachine.pauseThread();
    }
    private void changeColor(){

       // threadPlayMachine.setPaused(true);
        //threadPlayMachine.pauseThread();
        new AlertBox().chooseColor("Cambio de Color", "¡Elige un color para el contrincante!", "");

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
     * @param event the action event
     */
    @FXML
    void onHandleBack(ActionEvent event) {
        if (this.posInitCardToShow > 0) {
            this.posInitCardToShow--;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the "Next" button action to show the next set of cards.
     *
     * @param event the action event
     */
    @FXML
    void onHandleNext(ActionEvent event) {
        if (this.posInitCardToShow < this.humanPlayer.getCardsPlayer().size() - 4) {
            this.posInitCardToShow++;
            printCardsHumanPlayer();
        }
    }

    /**
     * Handles the action of taking a card.
     *
     * @param event the action event
     */
    @FXML
    void onHandleTakeCard(ActionEvent event) {
        // Implement logic to take a card here
        if (threadPlayMachine.getHasPlayerPlayed()){
            Card card = deck.takeCard();
            machinePlayer.addCard(card);
        }else{
            Card card = deck.takeCard();
            humanPlayer.addCard(card);
        }
    }

    /**
     * Handles the action of saying "Uno".
     *
     * @param event the action event
     */
    @FXML
    void onHandleUno(ActionEvent event) {
        // Implement logic to handle Uno event here
    }

    @FXML
    void onHandleQuitGame(ActionEvent event) {
        GameUnoStage.deleteInstance();
    }

}
