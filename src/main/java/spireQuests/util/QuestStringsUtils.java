package spireQuests.util;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.core.Settings;
import spireQuests.Anniv8Mod;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.megacrit.cardcrawl.core.Settings.GameLanguage.KOR;

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

    public static String formatLanguage(String rawText, String coloredText) {
        if (Settings.language == KOR) {
            String fix = "를";
            char last = coloredText.charAt(coloredText.length() - 1);
            if (last >= 0xAC00 && last <= 0xD7A3) {
                fix = ((last - 0xAC00) % 28 == 0) ? "를" : "을";
            }
            return String.format(rawText, coloredText, fix);
        }
        return String.format(rawText, coloredText);
    }
}
