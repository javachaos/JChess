package com.github.javachaos.jchess.gamelogic.managers;

import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.utils.ExceptionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayDeque;
import java.util.Deque;

public class SaveLoadManager {

    private static final Logger LOGGER = LogManager.getLogger(SaveLoadManager.class);

    private static final Type MOVE_TYPE = new TypeToken<Pair<Deque<Move>, Deque<Move>>>() {
    }.getType();

    private final String saveFilename;
    private final Gson gsonBuilder;
    private Deque<Move> undoMoves;
    private Deque<Move> redoMoves;

    private Pair<Deque<Move>, Deque<Move>> saveData;

    public SaveLoadManager(String saveFilename) {
        gsonBuilder = new GsonBuilder()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();
        this.saveFilename = saveFilename;
    }

    public void setUndoMoves(Deque<Move> undoMoves) {
        this.undoMoves = undoMoves;
    }

    public void setRedoMoves(Deque<Move> redoMoves) {
        this.redoMoves = redoMoves;
    }

    public void save() {
        File save = new File(saveFilename);
        saveData = new Pair<>(undoMoves, redoMoves);
        try (FileWriter ufw = new FileWriter(save)) {
            if (undoMoves != null && !undoMoves.isEmpty()) {
                LOGGER.info("Saving file: {}", save);
                gsonBuilder.toJson(saveData, ufw);
            }
        } catch (IOException e) {
            ExceptionUtils.log(e);
        }
    }

    public Pair<Deque<Move>, Deque<Move>> load() {
        Deque<Move> undos = new ArrayDeque<>();
        Deque<Move> redos = new ArrayDeque<>();
        Pair<Deque<Move>, Deque<Move>> loadData = new Pair<>(undos, redos);
        try (JsonReader reader = new JsonReader(new FileReader(saveFilename))) {
            loadData = gsonBuilder.fromJson(reader, MOVE_TYPE);
        } catch (IOException fnf) {
            ExceptionUtils.log(fnf);
        }
        return loadData;
    }
}
