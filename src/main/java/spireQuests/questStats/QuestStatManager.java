package spireQuests.questStats;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.utils.Logger;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.Anniv8Mod;

public class QuestStatManager {
    public static final String FAILED = "failed";
    public static final String COMPLETED = "completed";
    public static final String SEEN = "seen";
    public static final String TAKEN = "taken";
    public static final String CHARACTERS = "characters";
    private static final String[] INT_STATS = {SEEN, TAKEN, COMPLETED, FAILED};
    
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject rootJson;
    private static final String FILE_PATH = SpireConfig.makeFilePath(Anniv8Mod.modID, "questStats", "json");

    private static final Logger logger = new Logger(QuestStatManager.class.getSimpleName());
    
    private static final ArrayList<String> seenBuffer = new ArrayList<>();
    private static final ArrayList<String> takenBuffer = new ArrayList<>();
    private static final ArrayList<String> failedBuffer = new ArrayList<>();
    private static final ArrayList<String> completedBuffer = new ArrayList<>();
    private static final Map<String, String> charBuffer = new HashMap<>();
    private static File file;
    private static boolean doNotLog = false;
    

    public static void initialize() {

        file = new File(FILE_PATH);

        if (!file.exists()) {
            rootJson = new JsonObject();
            if (!saveRoot()) {
                logger.info("Error initializing Quest Stats json. Stats will not log...");
                doNotLog = true;
            }
        } else {
            loadRoot();
        }
    }

    private static void loadRoot() {
        try (FileReader reader = new FileReader(file)){
            rootJson = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Error loading Quest Stats json. Stats will not log...");
            doNotLog = true;
            rootJson = new JsonObject();
        }
    }

    private static boolean saveRoot() {
        if (doNotLog) {
            logger.error("ERROR: Cannot load Quest Stats json. Will not log quest data for this session...");
            return false;
        }
        try (FileWriter writer = new FileWriter(file)){
            gson.toJson(rootJson, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error saving Quest Stats json, skipping ...");
            return false;
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

        for (String e : INT_STATS) {
            if (!obj.has(e)) {
                obj.addProperty(e, 0);
            }
        }
        if (!obj.has(CHARACTERS)) {
            obj.add(CHARACTERS, new JsonArray());
        }
        
        return obj;
    }

    public static void markSeen(String questID) {
        if (Settings.isDebug) {
            logger.info(String.format("In debug mode: Not marking %s as seen", questID));
            return;
        }
        seenBuffer.add(questID);
    }
    public static void markTaken(String questID) {
        if (Settings.isDebug) {
            logger.info(String.format("In debug mode: Not marking %s as taken", questID));
            return;
        }
        takenBuffer.add(questID);
    }
    public static void markComplete(String questID) {
        if (Settings.isDebug) {
            logger.info(String.format("In debug mode: Not marking %s as complete", questID));
            return;
        }
        completedBuffer.add(questID);
        charBuffer.put(questID, AbstractDungeon.player.chosenClass.name());
    }
    public static void markFailed(String questID) {
        if (Settings.isDebug) {
            logger.info(String.format("In debug mode: Not marking %s as failed", questID));
            return;
        }
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

        for (Entry<String, String> e : charBuffer.entrySet()) {
            JsonObject obj = getAndValidateQuestObject(save, e.getKey());
            JsonArray array = obj.get(CHARACTERS).getAsJsonArray();
            boolean alreadyComplete = false;
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i).getAsString().equals(e.getValue())) {
                    alreadyComplete = true;
                    break;
                }
            }
            if (alreadyComplete) {
                continue;
            }
            array.add(e.getValue());
        }

        if (saveRoot()){
            resetBuffers();
        }
    }

    private static void resetBuffers() {
        seenBuffer.clear();
        takenBuffer.clear();
        completedBuffer.clear();
        failedBuffer.clear();
        charBuffer.clear();
    }

    public static JsonObject getStatsForQuest(String questID) {
        return getAndValidateQuestObject(getSaveJson(getRoot()), questID);
    }
}