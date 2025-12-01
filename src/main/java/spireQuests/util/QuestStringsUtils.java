package spireQuests.util;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import spireQuests.Anniv8Mod;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class QuestStringsUtils {
    private static final Map<String, QuestStrings> quests = new HashMap<>();

    public static void registerQuestStrings(String pathToFile) {
        String filePath = pathToFile + File.separator + "Queststrings.json";
        try {
            Gson gson = new Gson();
            String fileData = Gdx.files.internal(filePath).readString(String.valueOf(StandardCharsets.UTF_8));
            Type questsType = (new TypeToken<Map<String, QuestStrings>>(){}).getType();
            quests.putAll(gson.fromJson(fileData, questsType));
            Anniv8Mod.logger.info("Queststrings successfully loaded for: {}", pathToFile);
        } catch (Exception e) {
            Anniv8Mod.logger.error("Queststrings could not be loaded: {}", e.getLocalizedMessage());
        }
    }

    public static QuestStrings getQuestString(String questId) {
        return quests.getOrDefault(questId, null);
    }
}
