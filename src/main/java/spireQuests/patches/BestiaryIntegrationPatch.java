package spireQuests.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.megacrit.cardcrawl.core.Settings;
import javassist.CtBehavior;
import org.apache.logging.log4j.Logger;
import spireQuests.Anniv8Mod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@SpirePatch(
        cls = "Bestiary.database.MonsterDatabase",
        method = "load",
        optional = true
)
public class BestiaryIntegrationPatch {
    public static final Logger logger = Anniv8Mod.logger;

    @SpireInsertPatch(locator = Locator.class, localvars = {"arr"})
    public static void AddMonsters(Object __instance, JsonArray arr) {
        logger.info("Loading Bestiary entries for modded monsters");
        for (String packageName : Anniv8Mod.questPackages) {
            addPackageMonstersForLanguage(arr, packageName, "eng");
            if (Settings.language != Settings.GameLanguage.ENG)
            {
                addPackageMonstersForLanguage(arr, packageName, Settings.language.toString().toLowerCase());
            }
        }
    }

    private static void addPackageMonstersForLanguage(JsonArray arr, String packageName, String langKey) {
        String languageAndFolder = langKey + "/" + packageName;
        String filepath = Anniv8Mod.modID + "Resources/bestiary/" + languageAndFolder + "/bestiary.json";
        if (!Gdx.files.internal(filepath).exists()) {
            return;
        }
        InputStream in = BestiaryIntegrationPatch.class.getClassLoader().getResourceAsStream(filepath);

        if (in == null) {
            logger.error("Failed to load bestiary.json for package " + packageName + ", language " + langKey);
            return;
        }

        try {
            String content = resourceStreamToString(in);

            JsonObject o = new JsonParser().parse(content).getAsJsonObject();

            if (o.has("monsters") && o.get("monsters").isJsonArray()) {
                JsonArray additionalMonsters = o.getAsJsonArray("monsters");
                logger.info("Adding additional monsters for package " + packageName + ", language " + langKey + ": " + additionalMonsters.size());

                arr.addAll(additionalMonsters);
            }
        } catch (IOException e) {
            logger.error("Failed to load bestiary resource stream to string for package " + packageName + ", language " + langKey);
            e.printStackTrace();
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(JsonObject.class, "getAsJsonArray");
            int[] lines = LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            // We need to insert on the line after the call, so add 1
            for (int i = 0; i < lines.length; i++) {
                lines[i] = lines[i] + 1;
            }
            return lines;
        }
    }

    // Builds a string from an input stream
    private static String resourceStreamToString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String read;
        while ((read=br.readLine()) != null) {
            sb.append(read);
        }

        br.close();
        return sb.toString();
    }
}
