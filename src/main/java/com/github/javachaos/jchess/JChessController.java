package com.github.javachaos.jchess;

import com.github.javachaos.jchess.gamelogic.Board;
import com.github.javachaos.jchess.exceptions.JChessException;
import com.github.javachaos.jchess.gamelogic.GameStateManager;
import com.github.javachaos.jchess.gamelogic.pieces.core.AbstractPiece;
import com.github.javachaos.jchess.gamelogic.pieces.core.Piece;
import com.github.javachaos.jchess.gamelogic.pieces.core.PiecePos;

import com.github.javachaos.jchess.utils.ExceptionUtils;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.javachaos.jchess.gamelogic.GameStateManager.GameState.START;

public class JChessController {

    public static final Logger LOGGER = LogManager.getLogger(
            JChessController.class);
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
    private Button undoBtn;

    private Board board;

    private final StackPane[][] panes = new StackPane[8][8];

    private final List<StackPane> currentlySelected = new ArrayList<>();

    private StackPane currentSelection;

    private boolean pieceSelected;

    private final EnumMap<AbstractPiece.PieceType,
            Pair<Image, Image>> images = new EnumMap<>(AbstractPiece.PieceType.class);

    @FXML
    void initialize() {
        loadImages();
        board = new Board();
        board.start();
        assert exitBtn != null : "fx:id=\"exitBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert redoBtn != null : "fx:id=\"redoBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert undoBtn != null : "fx:id=\"undoBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert saveBtn != null : "fx:id=\"saveBtn\" was not injected: check your FXML file 'jchess.fxml'.";
        assert checkerGrid != null : "fx:id=\"checkerGrid\" was not injected: check your FXML file 'jchess.fxml'.";

        setupActions();
        generateTiles();
        checkerGrid.widthProperty().addListener(this::widthChanged);
        checkerGrid.heightProperty().addListener(this::heightChanged);
        Arrays.stream(panes).forEach(x -> Arrays.stream(x).forEach(n -> n.setOnMouseClicked(this::handlePressed)));
    }

    private void redrawPieces() {
        IntStream.range(0, 8).forEach(x ->
                IntStream.range(0, 8).forEach(y -> {
            Rectangle r = (Rectangle) panes[x][y].getChildren()
                    .filtered(Rectangle.class::isInstance).get(0);
            panes[x][y].getChildren().clear();
            panes[x][y].getChildren().add(r);
            board.getPiece((char)('a' + x), (char)('1' + y))
                    .ifPresent(p -> {
                        Bounds b = checkerGrid.getCellBounds(x, y);
                        ImageView img = getImageForPiece(p);
                        img.setFitHeight(b.getHeight());
                        img.setFitWidth(b.getWidth());
                        panes[x][y].getChildren().add(img);
                    });
        }));
    }

    private void generateTiles() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Rectangle r = new Rectangle();
                panes[x][y] = new StackPane();
                panes[x][y].getChildren().add(r);
                printLabelsX(x, y, r);
                printLabelsY(x, y, r);
                //Fill squares in checkered pattern
                r.setFill((x % 2 == 0 && y % 2 == 0) || (x % 2 != 0 && y % 2 != 0) ? Color.GRAY : Color.BLACK);
                r.setStroke(Color.BLACK);
                checkerGrid.add(panes[x][y], x, y);
                panes[x][y].setUserData(new PiecePos((char)('a' + x), (char)('1' + y)));
            }
        }
        LOGGER.info("Done generating tiles.");
    }

    private void printLabelsY(int x, int y, Rectangle r) {
        if (x == 0) {
            Label l = new Label(String.valueOf((char)('8' - y)));
            l.setTextFill(
                    y % 2 == 0 ? Color.BLACK : Color.GRAY);
            l.setPadding(new Insets(r.getWidth(), r.getHeight()+35,0,0));
            panes[x][y].getChildren().add(l);
        }
    }

    private void printLabelsX(int x, int y, Rectangle r) {
        if (y == 7) {
            Label l = new Label(String.valueOf((char)('a' + x)));
            l.setTextFill(
                    x % 2 == 0 ? Color.GRAY : Color.BLACK);
            l.setPadding(new Insets(r.getWidth(), r.getHeight()+50,0,0));
            panes[x][y].getChildren().add(l);
        }
    }

    public void widthChanged(Observable e) {
        if (GameStateManager.getInstance().isStart()) {
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
    }

    public void heightChanged(Observable e) {
        if (GameStateManager.getInstance().isStart()) {
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
    }

    void setupActions() {
        exitBtn.setOnAction(x -> exitApplication());
        newgameBtn.setOnAction(x -> createNewGame());
        undoBtn.setOnAction(x -> undoAction());
        redoBtn.setOnAction(x -> redoAction());
    }

    //------------------------------------------ Action Events ---------------------------------------------------------

    void createNewGame() {
        clearSelection();
        board.reset();
        GameStateManager.getInstance().setState(START);
        checkerGrid.autosize();
        redrawPieces();
    }

    void undoAction() {
        clearSelection();
        board.undo();
        redrawPieces();
    }

    void redoAction() {
        clearSelection();
        board.redo();
        redrawPieces();
    }

    void exitApplication() {
        Platform.exit();
    }

    private void clearSelection() {
        if (currentSelection != null) {
            currentSelection.setEffect(null);
            currentlySelected.forEach(x -> x.setEffect(null));
            currentlySelected.clear();
            pieceSelected = false;
        }
    }

    public void handlePressed(MouseEvent mouseEvent) {

        StackPane sp = (StackPane)mouseEvent.getSource();
        PiecePos p = (PiecePos) sp.getUserData();

        if(pieceSelected && currentlySelected.contains(sp)) {
            PiecePos from = (PiecePos) currentSelection.getUserData();
            
            try {
                board.movePiece(from, p);
            } catch (JChessException e) {
                ExceptionUtils.log(e);
            }
            redrawPieces();
        }
        clearSelection();

        if (p != null) {
            List<PiecePos> allPm = board.getPotentialMoves(p);
            allPm.forEach(pm -> {
                if (board.isOnBoard(pm)) {
                    currentlySelected.add(panes[pm.x() - 'a'][pm.y() - '1']);
                }
            });
            pieceSelected = true;
        }
        currentlySelected.forEach(x -> x.setEffect(getSelectedEffect()));
        currentSelection = sp;
        sp.setEffect(getSelectedEffect());
    }

    private Effect getSelectedEffect() {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(15.0);
        innerShadow.setChoke(0.65);
        innerShadow.setColor(Color.color(1.0, 1.0, 1.0)); // Set the color of the inner glow
        return innerShadow;
    }

    private InputStream getImg(String name) {
        return getClass().getResourceAsStream("/img/" + name + ".png");
    }

    private void loadImages() {
        images.put(AbstractPiece.PieceType.PAWN, new Pair<>(
                new Image(getImg("pawn_white")),
                new Image(getImg("pawn_black"))));
        images.put(AbstractPiece.PieceType.BISHOP, new Pair<>(
                new Image(getImg("bishop_white")),
                new Image(getImg("bishop_black"))));
        images.put(AbstractPiece.PieceType.ROOK, new Pair<>(
                new Image(getImg("rook_white")),
                new Image(getImg("rook_black"))));
        images.put(AbstractPiece.PieceType.KNIGHT, new Pair<>(
                new Image(getImg("knight_white")),
                new Image(getImg("knight_black"))));
        images.put(AbstractPiece.PieceType.KING, new Pair<>(
                new Image(getImg("king_white")),
                new Image(getImg("king_black"))));
        images.put(AbstractPiece.PieceType.QUEEN, new Pair<>(
                new Image(getImg("queen_white")),
                new Image(getImg("queen_black"))));
    }


    private ImageView getImageForPiece(Piece p) {
        if (p.isBlack()) {
            return new ImageView(images.get(p.getType()).getValue());
        } else {
            return new ImageView(images.get(p.getType()).getKey());
        }
    }
}
