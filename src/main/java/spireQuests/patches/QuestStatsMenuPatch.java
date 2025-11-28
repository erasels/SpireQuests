package spireQuests.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import javassist.CtBehavior;
import spireQuests.questStats.QuestStatsScreen;

public class QuestStatsMenuPatch {

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

    // Main Menu Button

    @SpirePatch2 (clz = MainMenuScreen.class, method = "setMainMenuButtons")
    public static class QuestMenuButton {
        @SpireInsertPatch(
            locator = MenuButtonLocator.class,
            localvars = {"index"}
        )
        public static void setMainMenuButtons(MainMenuScreen __instance, @ByRef int[] index) {
            __instance.buttons.add(new MenuButton(QUEST_STATS_BUTTON, index[0]));
            index[0]++;
        }

        private static class MenuButtonLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finMatcher = new Matcher.FieldAccessMatcher(Settings.class, "isShowBuild");
                return LineFinder.findInOrder(ctMethodToPatch, finMatcher);
            }
        }
    }

    @SpirePatch2 (clz = MenuButton.class, method = "setLabel")
    public static class SetMenuText {
        @SpirePostfixPatch
        public static void setLabel(MenuButton __instance, @ByRef String[] ___label) {
            if (__instance.result == QUEST_STATS_BUTTON) {
                ___label[0] = QuestStatsScreen.uiStrings.TEXT[0];
            }
        }
    }

    @SpirePatch2 (clz = MenuButton.class, method = "buttonEffect")
    public static class SetButtonEffect {
        @SpirePostfixPatch
        public static void buttonEffect(MenuButton __instance) {
            if (__instance.result == QUEST_STATS_BUTTON) {
                QuestStatsScreenField.statsScreen.get(CardCrawlGame.mainMenuScreen).open();
            }
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
