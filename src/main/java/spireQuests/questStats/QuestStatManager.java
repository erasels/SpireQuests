package spireQuests.questStats;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.utils.Logger;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import spireQuests.Anniv8Mod;

public class QuestStatManager {
    public static final String FAILED = "failed";
    public static final String COMPLETED = "completed";
    public static final String SEEN = "seen";
    public static final String TAKEN = "taken";
    private static final String[] STAT_ENTRIES = {SEEN, TAKEN, COMPLETED, FAILED};
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject rootJson;
    private static String FILE_PATH = SpireConfig.makeFilePath(Anniv8Mod.modID, "questStats", "json");

    private static Logger logger = new Logger(QuestStatManager.class.getSimpleName());
    
    private static ArrayList<String> seenBuffer = new ArrayList<>();
    private static ArrayList<String> takenBuffer = new ArrayList<>();
    private static ArrayList<String> failedBuffer = new ArrayList<>();
    private static ArrayList<String> completedBuffer = new ArrayList<>();
    private static File file;
    private static boolean doNotLog = false;
    

    public static void initialize() {

        file = new File(FILE_PATH);

        if (!file.exists()) {
            rootJson = new JsonObject();
            saveRoot();
        } else {
            loadRoot();
        }
    }

    private static void loadRoot() {
        try (FileReader reader = new FileReader(file)){
            rootJson = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Error loading Quest Stats json. Creating new JsonObject. Stats will not log...");
            doNotLog = true;
            rootJson = new JsonObject();
        }
    }

    private static void saveRoot() {
        if (doNotLog) {
            logger.error("ERROR: Cannot load Quest Stats json. Will not log quest data for this session...");
            return;
        }
        try (FileWriter writer = new FileWriter(file)){
            gson.toJson(rootJson, writer);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error saving Quest Stats json, skipping ...");
        }
    }

    private static JsonObject getRoot() {
        return rootJson;
    }

    private static JsonObject getSaveJson(JsonObject root) {
        return getSaveJson(root, CardCrawlGame.saveSlot);
    }

    private static JsonObject getSaveJson(JsonObject root, int saveSlot) {
        String key = "save" + Integer.toString(saveSlot);
        if (!root.has(key)) {
            root.add(key, new JsonObject());
        }
        return root.getAsJsonObject(key);
    }

    private static JsonObject getAndValidateQuestObject(JsonObject saveJson, String questId) {
        if (!saveJson.has(questId)) {
            saveJson.add(questId, new JsonObject());
        }
        JsonObject obj = saveJson.getAsJsonObject(questId);

        for (String e : STAT_ENTRIES) {
            if (!obj.has(e)) {
                obj.addProperty(e, 0);
            }
        }

        return obj;
    }

    public static void markSeen(String questID) {
        seenBuffer.add(questID);
    }
    public static void markTaken(String questID) {
        takenBuffer.add(questID);
    }
    public static void markComplete(String questID) {
        completedBuffer.add(questID);
    }
    public static void markFailed(String questID) {
        failedBuffer.add(questID);
    }
    
    public static void commitStats() {
        JsonObject save = getSaveJson(rootJson);

        for (String q : seenBuffer) {
            JsonObject obj = getAndValidateQuestObject(save, q);
            obj.addProperty(SEEN, obj.get(SEEN).getAsInt() + 1);
        }
        for (String q : takenBuffer) {
            JsonObject obj = getAndValidateQuestObject(save, q);
            obj.addProperty(TAKEN, obj.get(TAKEN).getAsInt() + 1);
        }
        for (String q : completedBuffer) {
            JsonObject obj = getAndValidateQuestObject(save, q);
            obj.addProperty(COMPLETED, obj.get(COMPLETED).getAsInt() + 1);
        }
        for (String q : failedBuffer) {
            JsonObject obj = getAndValidateQuestObject(save, q);
            obj.addProperty(FAILED, obj.get(FAILED).getAsInt() + 1);
        }

        saveRoot();
        resetBuffers();
    }

    private static void resetBuffers() {
        seenBuffer.clear();
        takenBuffer.clear();
        completedBuffer.clear();
        failedBuffer.clear();
    }

    public static JsonObject getStatsForQuest(String questID) {
        return getAndValidateQuestObject(getSaveJson(getRoot()), questID);
    }
}