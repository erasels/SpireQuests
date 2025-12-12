package spireQuests.questStats.patches;

import java.lang.reflect.Field;

import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.integrations.DistributorFactory;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton.PanelClickResult;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuPanelButton.PanelColor;
import com.megacrit.cardcrawl.screens.mainMenu.MenuPanelScreen.PanelScreen;

import basemod.ReflectionHacks;
import spireQuests.Anniv8Mod;
import spireQuests.questStats.patches.QuestStatsScreenPatch.QuestStatsScreenField;
import spireQuests.util.TexLoader;

public class QuestStatsMenuPatch {
    public static class Enums {
            @SpireEnum
            public static MainMenuPanelButton.PanelClickResult QUEST_STATS;
    }

    @SpirePatch2(clz = MenuPanelScreen.class, method = "initializePanels")
    public static class InitStatsPanel {
        public static void Postfix(MenuPanelScreen __instance, MenuPanelScreen.PanelScreen ___screen) {
            if (___screen == PanelScreen.STATS) {
                __instance.panels.clear();

                float offset;
                float y = (Settings.HEIGHT / 2.0F) + 50.0F * Settings.scale;
                if (!DistributorFactory.isLeaderboardEnabled()) {
                    offset = 450.0F;
                    __instance.panels.add(new MainMenuPanelButton(PanelClickResult.STAT_CHAR, PanelColor.BLUE, Settings.WIDTH / 2.0F - offset * Settings.scale, y));
                    __instance.panels.add(new MainMenuPanelButton(PanelClickResult.STAT_HISTORY, PanelColor.RED, Settings.WIDTH / 2.0F * Settings.scale, y));
                    __instance.panels.add(new MainMenuPanelButton(Enums.QUEST_STATS, PanelColor.BEIGE, Settings.WIDTH / 2.0F + offset * Settings.scale, y));
                } else {
                    offset = 225.0F;
                    __instance.panels.add(new MainMenuPanelButton(PanelClickResult.STAT_CHAR, PanelColor.BLUE, Settings.WIDTH / 2.0F - 3 * offset * Settings.scale, y));
                    __instance.panels.add(new MainMenuPanelButton(PanelClickResult.STAT_HISTORY, PanelColor.RED, Settings.WIDTH / 2.0F - offset * Settings.scale, y));
                    __instance.panels.add(new MainMenuPanelButton(PanelClickResult.STAT_LEADERBOARDS, PanelColor.BEIGE, Settings.WIDTH / 2.0F + offset * Settings.scale, y));
                    __instance.panels.add(new QuestStatsMainMenuPanel(Enums.QUEST_STATS, PanelColor.BEIGE, Settings.WIDTH / 2.0F + 3 * offset * Settings.scale, y));
                }
                SpireReturn.Return();
            }
        }
    }

    public static class QuestStatsMainMenuPanel extends MainMenuPanelButton {
        private static final Texture BANNER_TOP = TexLoader.getTexture(Anniv8Mod.makeUIPath("stats/panel_image.png"));
        public static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(Anniv8Mod.makeID(QuestStatsMainMenuPanel.class.getSimpleName()));
        private static Field resultField = null;

        public QuestStatsMainMenuPanel(PanelClickResult setResult, PanelColor setColor, float x, float y) {
            super(setResult, setColor, x, y);
        }

        @SpireOverride
        protected void setLabel() {
            if (getResult() == Enums.QUEST_STATS) {
                ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "panelImg", ImageMaster.MENU_PANEL_BG_BEIGE);
                ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "portraitImg", BANNER_TOP);
                ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "header", uiStrings.TEXT[0]);
                ReflectionHacks.setPrivate(this, MainMenuPanelButton.class, "description", uiStrings.TEXT[1]);
            } else {
                SpireSuper.call();
            }
        }

        @SpireOverride
        protected void buttonEffect() {
            if (getResult() == Enums.QUEST_STATS) {
                QuestStatsScreenField.statsScreen.get(CardCrawlGame.mainMenuScreen).open();
            } else {
                SpireSuper.call();
            }
        }

        private PanelClickResult getResult() {
            if (resultField == null) {
                try {
                    resultField = MainMenuPanelButton.class.getDeclaredField("result");
                    resultField.setAccessible(true);
                } catch (NoSuchFieldException | SecurityException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            try {
                return (PanelClickResult) resultField.get(this);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }
}
