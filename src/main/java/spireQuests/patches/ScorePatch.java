package spireQuests.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverScreen;
import com.megacrit.cardcrawl.screens.GameOverStat;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

import static spireQuests.Anniv8Mod.makeID;

public class ScorePatch {
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(makeID("Score")).TEXT;
    private static final int POINTS_PER_QUEST = 20;

    @SpirePatch2(clz = GameOverScreen.class, method = "calcScore")
    public static class CalcScorePatch {
        @SpirePostfixPatch
        public static int patch(boolean victory, int __result) {
            return __result + countCompletedQuests() * POINTS_PER_QUEST;
        }
    }

    @SpirePatch2(clz = VictoryScreen.class, method = "createGameOverStats")
    public static class VictoryScreenPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(VictoryScreen __instance) {
            int count = countCompletedQuests();
            if (count > 0) {
                ArrayList<GameOverStat> stats = ReflectionHacks.getPrivate(__instance, GameOverScreen.class, "stats");
                stats.add(new GameOverStat(String.format(TEXT[0], count), "", Integer.toString(countCompletedQuests() * POINTS_PER_QUEST)));
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(VictoryScreen.class, "IS_POOPY");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = DeathScreen.class, method = "createGameOverStats")
    public static class DeathScreenPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void patch(DeathScreen __instance) {
            int count = countCompletedQuests();
            if (count > 0) {
                ArrayList<GameOverStat> stats = ReflectionHacks.getPrivate(__instance, GameOverScreen.class, "stats");
                stats.add(new GameOverStat(String.format(TEXT[0], count), "", Integer.toString(countCompletedQuests() * POINTS_PER_QUEST)));
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(DeathScreen.class, "IS_POOPY");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    private static int countCompletedQuests() {
        List<List<String>> questCompletionPerFloor = QuestRunHistoryPatch.questCompletionPerFloorLog.get(AbstractDungeon.player);
        if (questCompletionPerFloor != null) {
            return (int)questCompletionPerFloor.stream().mapToLong(List::size).sum();
        }
        return 0;
    }
}
