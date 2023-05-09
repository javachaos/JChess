package com.github.javachaos.jchess;

import com.github.javachaos.jchess.gamelogic.Alerts;
import com.github.javachaos.jchess.gamelogic.managers.SaveLoadManager;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.ai.player.Player;
import com.github.javachaos.jchess.gamelogic.states.core.ChessGame;
import com.github.javachaos.jchess.gamelogic.states.core.GameState;
import com.github.javachaos.jchess.gamelogic.states.impl.*;
import com.github.javachaos.jchess.utils.Constants;
import com.github.javachaos.jchess.utils.ImageLoader;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;

public class JChessController {

    public static final Logger LOGGER = LogManager.getLogger(
            JChessController.class);
    private List<StackPane> highlightedPanes;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @FXML
    public Button newgameBtn;
    @FXML
    private GridPane checkerGrid;
    @FXML
    private Button exitBtn;
    @FXML
    private Button redoBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button loadBtn;
    @FXML
    private Button undoBtn;
    @FXML
    private Label gameStatus;

    private StackPane currentSelection;
    private boolean pieceSelected;
    private SaveLoadManager saveLoadManager;
    private ImageLoader imLoader;

    private Move blacksNextMove;
    private Move whitesNextMove;

    private GameState currentState;

    private Move nextMove;
    private ChessGame chessGame;

    /**
     * Get the next requested move for player p
     * @param p
     */
    public Move getMove(Player p) {
        if (p == Player.BLACK) {
            nextMove = blacksNextMove;
        }

        if (p == Player.WHITE) {
            nextMove = whitesNextMove;
        }

        return nextMove;
    }

    @FXML
    void initialize() {
        initLists();
        initFiles();
        initGame();
        initComponents();
        generateTiles();
        setupActions();
    }

    private void initComponents() {
        assert exitBtn != null : "fx:id=\"exitBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert redoBtn != null : "fx:id=\"redoBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert undoBtn != null : "fx:id=\"undoBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert saveBtn != null : "fx:id=\"saveBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert loadBtn != null : "fx:id=\"loadBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert checkerGrid != null : "fx:id=\"checkerGrid\" was not injected: check your FXML file 'jchess.fxml'.";
    }

    private void initGame() {
        chessGame = new ChessGame(this);
    }

    private void initLists() {
        highlightedPanes = new ArrayList<>();
    }

    private void initFiles() {
        imLoader = new ImageLoader();
        saveLoadManager = new SaveLoadManager(Constants.UNDO_SAVEFILE,
                Constants.REDO_SAVEFILE);
    }

    private void redrawPieces() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                drawCheckeredTiles(x, y);
                int finalX = x;
                int finalY = y;
                chessGame.getBoard().getPiece((char) ('a' + x), (char) ('1' + y))
                        .ifPresent(p -> renderPiece(finalX, finalY, p));
            }
        }
    }

    private void renderPiece(int x, int y, Piece p) {
        Bounds b = checkerGrid.getCellBounds(x, y);
        ImageView img = imLoader.getImageForPiece(p);
        img.setFitHeight(b.getHeight());
        img.setFitWidth(b.getWidth());
        StackPane stackPane = getPane(x, y);
        if (stackPane != null) {
            stackPane.getChildren().add(img);
        }
    }

    private void drawCheckeredTiles(int x, int y) {
        StackPane stackPane = getPane(x, y);
        if (stackPane != null) {
            Rectangle r = (Rectangle) stackPane.getChildren()
                    .filtered(Rectangle.class::isInstance).get(0);
            stackPane.getChildren().clear();
            stackPane.getChildren().add(r);
        }
    }

    private void generateTiles() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                StackPane stackPane = drawTile(x, y);
                checkerGrid.add(stackPane, x, y);
                PiecePos pp = new PiecePos((char) ('a' + x), (char) ('1' + y));
                LOGGER.debug("Adding tile: {}", pp);
                stackPane.setUserData(pp);
            }
        }
        LOGGER.info("Done generating tiles.");
    }

    private StackPane drawTile(int x, int y) {
        Rectangle r = new Rectangle();
        StackPane sp = new StackPane();
        sp.getChildren().add(r);
        //Fill squares in checkered pattern
        r.setFill((x % 2 == 0 && y % 2 == 0)
                || (x % 2 != 0 && y % 2 != 0) ? Color.GRAY : Color.BLACK);
        r.setStroke(Color.BLACK);
        return sp;
    }

    private StackPane getPane(int x, int y) {
        return (StackPane) checkerGrid.getChildren().stream()
                .filter(c -> GridPane.getColumnIndex(c) == x && GridPane.getRowIndex(c) == y)
                .findFirst()
                .orElse(null);
    }

    public void widthChanged(Observable e) {
        checkerGrid.getChildren().forEach(x -> {
            if (x instanceof StackPane r) {
                r.getChildren().forEach(c -> {
                    if (c instanceof Rectangle n) {
                        n.setWidth(checkerGrid.getWidth() / checkerGrid.getColumnCount());
                    }
                });
            }
        });
    }

    public void heightChanged(Observable e) {
        checkerGrid.getChildren().forEach(x -> {
            if (x instanceof StackPane r) {
                r.getChildren().forEach(c -> {
                    if (c instanceof Rectangle n) {
                        n.setHeight(checkerGrid.getHeight() / checkerGrid.getRowCount());
                    }
                });
            }
        });
    }

    void setupActions() {
        exitBtn.setOnAction(x -> exitApplication());
        newgameBtn.setOnAction(x -> createNewGame());
        undoBtn.setOnAction(x -> undoAction());
        redoBtn.setOnAction(x -> redoAction());
        saveBtn.setOnAction(x -> saveButtonHandler());
        loadBtn.setOnAction(x -> loadButtonHandler());
        checkerGrid.widthProperty().addListener(this::widthChanged);
        checkerGrid.heightProperty().addListener(this::heightChanged);
        checkerGrid.getChildren().filtered(StackPane.class::isInstance)
                .forEach(g -> g.setOnMouseClicked(this::handlePressed));
    }

    void createNewGame() {
        LOGGER.info("Creating new game.");
        clearSelection();
        chessGame.getBoard().reset();
        checkerGrid.autosize();
        redrawPieces();
        currentState = chessGame.getCurrentState();
        currentState.handle();//Start state -> Whites Turn state
    }

    void loadButtonHandler() {
        LOGGER.info("Loading saved game.");
        Deque<Move> redos = saveLoadManager.loadRedos();
        Deque<Move> undos = saveLoadManager.loadUndos();
        if (undos != null) {
            clearSelection();
            chessGame.getBoard().reset();
            //chessGame.setUndos(undos);
            //chessGame.setRedos(redos);
            while (!undos.isEmpty()) {
                Move m = undos.pollLast();
                chessGame.getBoard().doMove(m);
            }
            redrawPieces();
        }
    }

    void undoAction() {
        LOGGER.info("User selected undo.");
        clearSelection();
        chessGame.undo();
        redrawPieces();
    }

    void redoAction() {
        LOGGER.info("User selected redo.");
        clearSelection();
        chessGame.redo();
        redrawPieces();
    }

    void exitApplication() {
        LOGGER.info("User requested exit.");
        Platform.exit();
    }

    void saveButtonHandler() {
        LOGGER.info("Saving game.");
        //saveLoadManager.setUndoMoves(chessGame.getUndos());
        //saveLoadManager.setRedoMoves(chessGame.getRedos());
        saveLoadManager.save();
    }

    private void clearSelection() {
        if (currentSelection != null) {
            currentSelection.setEffect(null);
            highlightedPanes.forEach(x -> x.setEffect(null));
            highlightedPanes.clear();
            pieceSelected = false;
        }
    }

    private void highlightSquares(StackPane selectedPane, PiecePos selectedPiece) {
        if (selectedPiece != null) {
            List<PiecePos> potentialMoves = chessGame.getBoard().getPotentialMoves(selectedPiece);
            potentialMoves.forEach(potentialMove -> {
                if (chessGame.getBoard().isOnBoard(potentialMove)) {
                    StackPane stackPane =
                            getPane(potentialMove.x() - 'a',
                                    potentialMove.y() - '1');
                    highlightedPanes.add(stackPane);
                }
            });
            pieceSelected = true;
        }
        applySelectedEffect(selectedPane);
    }

    private void attemptPlayerMove(StackPane selectedPane, PiecePos p, ChessGame cg) {
        if (pieceSelected && highlightedPanes.contains(selectedPane)) {
            PiecePos from = (PiecePos) currentSelection.getUserData();
            Piece piece = cg.getBoard().getPiece(from).orElseThrow();
            AbstractPiece.PieceType pt = piece.getType();
            Player player = piece.getPlayer();
            whitesNextMove = new Move(from, p, pt, player);
        }
    }

    private void doAITurn() {
        blacksNextMove = chessGame.getBoard().getAIMove();
        clearSelection();
        redrawPieces();
    }

    public void handlePressed(MouseEvent mouseEvent) {
        StackPane selectedPane = (StackPane) mouseEvent.getSource();
        PiecePos selectedPiece = (PiecePos) selectedPane.getUserData();
        currentState = chessGame.getCurrentState();
        currentState.handle();
        if (currentState instanceof WhitesTurnState) {
            gameStatus.setText("Whites turn.");
            attemptPlayerMove(selectedPane, selectedPiece, chessGame);
            clearSelection();
            highlightSquares(selectedPane, selectedPiece);
            currentState.handle();
        }
        if (currentState instanceof WhiteCheckState)  {
            gameStatus.setText("White in check!");
            currentState.handle();
        }
        if (currentState instanceof WhiteWinState)  {
            gameStatus.setText("White won!");
            Alerts.info("White Wins.");
            currentState.handle();
        }
        if (currentState instanceof BlacksTurnState)  {
            gameStatus.setText("Blacks turn.");
            doAITurn();
            currentState.handle();
        }
        if (currentState instanceof BlackCheckState)  {
            gameStatus.setText("Black in check!");
            currentState.handle();
        }
        if (currentState instanceof BlackWinState)  {
            gameStatus.setText("Black won!");
            Alerts.info("Black Wins.");
            currentState.handle();
        }
        if (currentState instanceof StalemateState)  {
            gameStatus.setText("Draw.");
            Alerts.info("Draw.");
            currentState.handle();
        }
        redrawPieces();
    }

    private void applySelectedEffect(StackPane selectedPane) {
        highlightedPanes.forEach(x -> x.setEffect(getSelectedEffect()));
        currentSelection = selectedPane;
        selectedPane.setEffect(getSelectedEffect());
    }

    private Effect getSelectedEffect() {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(35.0);
        innerShadow.setChoke(0.25);
        // Set the color of the inner glow
        innerShadow.setColor(Color.color(0.0, 1.0, 1.0));
        return innerShadow;
    }

    public void setMove(Move m) {
        this.nextMove = m;
    }
}
