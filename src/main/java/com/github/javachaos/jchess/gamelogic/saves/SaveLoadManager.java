package com.github.javachaos.jchess.gamelogic.saves;

import com.github.javachaos.jchess.gamelogic.pieces.core.Move;
import com.github.javachaos.jchess.utils.ExceptionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
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

public class SaveLoadManager implements ISaveLoader {

    private static final Logger LOGGER = LogManager.getLogger(SaveLoadManager.class);

    private static final Type MOVE_TYPE = new TypeToken<UndoData>() {
    }.getType();

    private final String saveFilename;
    private final Gson gsonBuilder;
    private UndoData undoData;

    public SaveLoadManager(String saveFilename) {
        gsonBuilder = new GsonBuilder()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();
        this.saveFilename = saveFilename;
    }

    public void setSaveData(UndoData undoData) {
        this.undoData = undoData;
    }
    public void save() {
        if (undoData == null) {
            return;
        }
        File save = new File(saveFilename);
        try (FileWriter ufw = new FileWriter(save)) {
            if (undoData.getUndoList() != null && !undoData.getUndoList().isEmpty()) {
                LOGGER.info("Saving file: {}", save);
                gsonBuilder.toJson(undoData, ufw);
            }
        } catch (IOException e) {
            ExceptionUtils.log(e);
        }
    }

    public UndoData load() {
        Deque<Move> undos = new ArrayDeque<>();
        Deque<Move> redos = new ArrayDeque<>();
        UndoData loadData = new UndoData(undos, redos);
        try (JsonReader reader = new JsonReader(new FileReader(saveFilename))) {
            loadData = gsonBuilder.fromJson(reader, MOVE_TYPE);
        } catch (IOException fnf) {
            ExceptionUtils.log(fnf);
        }
        return loadData;
    }
}
