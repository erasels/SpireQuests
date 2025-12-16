package spireQuests.questStats.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.scenes.TitleBackground;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

import basemod.ReflectionHacks;
import spireQuests.Anniv8Mod;
import spireQuests.questStats.QuestStats;
import spireQuests.questStats.StatisticsFTUE;

// Straight up copy and pasted from Packmaster FTUE.
public class QuestStatsFTUEPatch {

    //FTUE Logic
    private static StatisticsFTUE ftue;

    @SpirePatch2(clz = TitleBackground.class, method = "update")
    public static class MenuMenuOpen {
        @SpirePostfixPatch
        public static void patch(TitleBackground __instance) {
            if(__instance.activated && ((float) ReflectionHacks.getPrivate(__instance, TitleBackground.class, "timer")) <= 0 && !Anniv8Mod.isStatsFTUEComplete() && QuestStats.hasFinishedAnyQuest()) {
                Anniv8Mod.completeStatsFTUE();
                ftue = new StatisticsFTUE(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F);
            }
        }
    }

    @SpirePatch2(clz = MainMenuScreen.class, method = "update")
    public static class FTUEUpdate {
        @SpirePrefixPatch
        public static SpireReturn<?> patch(MainMenuScreen __instance) {
            if(ftue != null) {
                //Darken logic
                __instance.screenColor.a = MathHelper.popLerpSnap(__instance.screenColor.a, 0.8F);
                if(ftue.isDone) {
                    __instance.lighten();
                    ftue = null;
                    return SpireReturn.Continue();
                }

                ftue.update();
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = MainMenuScreen.class, method = "render")
    public static class FTUERender {
        @SpirePostfixPatch
        public static void patch(SpriteBatch sb) {
            if(ftue != null) {
                ftue.render(sb);
            }
        }
    }
}
