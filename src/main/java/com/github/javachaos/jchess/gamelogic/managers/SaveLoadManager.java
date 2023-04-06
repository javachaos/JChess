package com.github.javachaos.jchess.gamelogic.managers;

import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.utils.ExceptionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayDeque;
import java.util.Deque;

public class SaveLoadManager {

    private static final Logger LOGGER = LogManager.getLogger(SaveLoadManager.class);

    private static final Type MOVE_TYPE = new TypeToken<Deque<Move>>() {
    }.getType();

    private final String undoFilename;
    private final String redoFilename;

    private Deque<Move> undoMoves;
    private Deque<Move> redoMoves;

    private final Gson gsonBuilder;

    public SaveLoadManager(String undoFilename, String redoFilename) {
        gsonBuilder = new GsonBuilder()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();
        this.undoFilename = undoFilename;
        this.redoFilename = redoFilename;
    }

    public void setUndoMoves(Deque<Move> undoMoves) {
        this.undoMoves = undoMoves;
    }

    public void setRedoMoves(Deque<Move> redoMoves) {
        this.redoMoves = redoMoves;
    }

    public void save() {
        File u = new File(undoFilename);
        File r = new File(redoFilename);
        try (FileWriter ufw = new FileWriter(u);
             FileWriter rfw = new FileWriter(r)) {
            if (undoMoves != null && !undoMoves.isEmpty()) {
                LOGGER.info("Saving file: {}", u);
                gsonBuilder.toJson(undoMoves, ufw);
            }
            if (redoMoves != null && !redoMoves.isEmpty()) {
                LOGGER.info("Saving file: {}", r);
                gsonBuilder.toJson(redoMoves, rfw);
            }
        } catch (IOException e) {
            ExceptionUtils.log(e);
        }
    }

    public Deque<Move> loadUndos() {
        Deque<Move> undos = new ArrayDeque<>();
        try (JsonReader reader = new JsonReader(new FileReader(undoFilename))) {
            undos = gsonBuilder.fromJson(reader, MOVE_TYPE);
        } catch (IOException fnf) {
            ExceptionUtils.log(fnf);
        }
        return undos;
    }

    public Deque<Move> loadRedos() {
        Deque<Move> redos = new ArrayDeque<>();
        try (JsonReader reader = new JsonReader(new FileReader(redoFilename))) {
            redos = gsonBuilder.fromJson(reader, MOVE_TYPE);
        } catch (IOException fnf) {
            ExceptionUtils.log(fnf);
        }
        return redos;
    }
}
