package com.github.javachaos.jchess;

import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.ChessBoard;
import com.github.javachaos.jchess.gamelogic.managers.GSM;
import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;
import com.github.javachaos.jchess.gamelogic.player.MinimaxAIPlayer;
import com.github.javachaos.jchess.gamelogic.player.Player;
import com.github.javachaos.jchess.gamelogic.saves.ISaveLoader;
import com.github.javachaos.jchess.gamelogic.saves.SaveLoadManager;
import com.github.javachaos.jchess.gamelogic.saves.SaveData;
import com.github.javachaos.jchess.gamelogic.saves.VoidSaveLoadManager;
import com.github.javachaos.jchess.utils.Constants;
import com.github.javachaos.jchess.utils.ExceptionUtils;
import com.github.javachaos.jchess.utils.ImageLoader;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class JChessController {

    public static final Logger LOGGER = LogManager.getLogger(
            JChessController.class);
    private List<StackPane> highlightedPanes;

    private Window windowRef;

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
    private ChessBoard board;
    private StackPane currentSelection;
    private boolean pieceSelected;
    private ImageLoader imLoader;

    @FXML
    void initialize() {
        initLists();
        initFiles();
        initChessBoard();
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

    private void initChessBoard() {
        board = new ChessBoard(new MinimaxAIPlayer(Player.BLACK));
        board.start();
    }

    private void initLists() {
        highlightedPanes = new ArrayList<>();
    }

    private void initFiles() {
        imLoader = new ImageLoader();
    }

    private void redrawPieces() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                drawCheckeredTiles(x, y);
                int finalX = x;
                int finalY = y;
                board.getPiece((char) ('a' + x), (char) ('1' + y))
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
        board.reset();
        checkerGrid.autosize();
        redrawPieces();
    }

    void loadButtonHandler() {
        LOGGER.info("Loading saved game.");
        SaveData udd = chooseFile(false).load();
        Deque<Move> redos = udd.undoList();
        Deque<Move> undos = udd.redoList();
        if (undos != null) {
            clearSelection();
            GSM.instance().setTurn(board.getActivePlayer());
            GSM.instance().setUndos(undos);
            GSM.instance().setRedos(redos);
            board.setCapturedPieces(udd.captives());
            board.applyFen(udd.fen());
            redrawPieces();
        }
    }

    void undoAction() {
        LOGGER.info("User selected undo.");
        clearSelection();
        GSM.instance().undo(board);
        redrawPieces();
    }

    void redoAction() {
        LOGGER.info("User selected redo.");
        clearSelection();
        GSM.instance().redo(board);
        redrawPieces();
    }

    void exitApplication() {
        LOGGER.info("User requested exit.");
        Platform.exit();
    }

    void saveButtonHandler() {
        LOGGER.info("Saving game.");
        ISaveLoader sl = chooseFile(true);
        sl.save();
    }

    public void setStage(Window windowRef) {
        this.windowRef = windowRef;
    }

    /**
     * Private helper method to show a file chooser with .json extension filter
     * then return a new {@link SaveLoadManager} from the selected file.
     *
     * @param save true if the chooser is intended to save a new game save.
     * @return a new {@link SaveLoadManager}
     */
    private ISaveLoader chooseFile(boolean save) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save/Load Game");
        fc.setInitialDirectory(Constants.DEFAULT_SAVE_DIR);
        fc.setInitialFileName("jchess.json");
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("JSON", ".json"));
        File selectedFile;
        if (save) {
            selectedFile = fc.showSaveDialog(windowRef);
            SaveData sd = new SaveData(GSM.instance().getUndos(), GSM.instance().getRedos(),
                    board.getCapturedPieces(), board.getFenString());
            SaveLoadManager slm = new SaveLoadManager(selectedFile.getAbsolutePath());
            slm.setSaveData(sd);
            return slm;
        } else {
            selectedFile = fc.showOpenDialog(windowRef);
        }

        if (selectedFile == null) {
            return new VoidSaveLoadManager();
        }

        return new SaveLoadManager(selectedFile.getAbsolutePath());
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
            List<PiecePos> potentialMoves = board.getPotentialMoves(selectedPiece);
            potentialMoves.forEach(potentialMove -> {
                if (board.isOnBoard(potentialMove)) {
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

    private void attemptPlayerMove(StackPane selectedPane, PiecePos p) {
        if (pieceSelected && highlightedPanes.contains(selectedPane) &&
        GSM.instance().isPlayerTurn()) {
            PiecePos from = (PiecePos) currentSelection.getUserData();
            try {
                board.movePiece(from, p);
            } catch (JChessException e) {
                ExceptionUtils.log(e);
            }
            redrawPieces();
        }
    }

    private void doAITurn() {
        if (GSM.instance().isAITurn()) {
            board.doAIMove();
            clearSelection();
            redrawPieces();
        }
    }

    public void handlePressed(MouseEvent mouseEvent) {
        StackPane selectedPane = (StackPane) mouseEvent.getSource();
        PiecePos selectedPiece = (PiecePos) selectedPane.getUserData();

        attemptPlayerMove(selectedPane, selectedPiece);
        clearSelection();
        highlightSquares(selectedPane, selectedPiece);

        doAITurn();
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

}
