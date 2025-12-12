package spireQuests.questStats.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import spireQuests.questStats.QuestStatsScreen;

public class QuestStatsScreenPatch {

    @SpireEnum
    public static MenuButton.ClickResult QUEST_STATS_BUTTON;

    public static final String[] TEXT = null;

    // Add new screen

    @SpirePatch (clz = MainMenuScreen.class, method = SpirePatch.CLASS)
    public static class QuestStatsScreenField {
        public static SpireField<QuestStatsScreen> statsScreen = new SpireField<>(() -> null);
    }

    @SpirePatch2 (clz = MainMenuScreen.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {boolean.class})
    private static class AddNewScreenField {
        @SpirePostfixPatch
        public static void addScreen(MainMenuScreen __instance) {
            QuestStatsScreenField.statsScreen.set(__instance, new QuestStatsScreen());
        }
    }

    // Update & Render
    @SpirePatch2 (clz = MainMenuScreen.class, method = "update")
    public static class UpdateQuestStatsScreen {
        @SpirePostfixPatch
        public static void Update(MainMenuScreen __instance) {
            if (__instance.screen == QuestStatsScreen.Enum.QUEST_STATS_SCREEN) {
                QuestStatsScreenField.statsScreen.get(__instance).update();
            }
        }
    }

    @SpirePatch2 (clz = MainMenuScreen.class, method = "render")
    public static class RenderQuestStatsScreen {
        @SpirePostfixPatch
        public static void Render(MainMenuScreen __instance, SpriteBatch sb) {
            if (__instance.screen == QuestStatsScreen.Enum.QUEST_STATS_SCREEN) {
                QuestStatsScreenField.statsScreen.get(__instance).render(sb);
            }
        }
    }
    
}
