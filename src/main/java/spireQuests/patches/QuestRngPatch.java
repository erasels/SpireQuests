package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import javassist.CtBehavior;
import spireQuests.quests.AbstractQuest;

public class QuestRngPatch {
    private static final int PRE_COMBAT_SEED = 87697;
    private static final int POST_COMBAT_SEED = 10957;

    @SpirePatch2(clz = AbstractDungeon.class, method = "generateSeeds")
    public static class InitializeQuestRngPatch {
        @SpirePostfixPatch
        public static void initialize() {
            AbstractQuest.rng = new Random(Settings.seed + PRE_COMBAT_SEED);
        }
    }
    @SpirePatch2(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = { SaveFile.class })
    public static class ResetQuestRngOnEnterOrReloadPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void reset(SaveFile saveFile) {
            boolean isLoadingPostCombatSave = CardCrawlGame.loadingSave && saveFile != null && saveFile.post_combat;
            AbstractQuest.rng = new Random(Settings.seed + AbstractDungeon.floorNum + (isLoadingPostCombatSave ? POST_COMBAT_SEED : PRE_COMBAT_SEED));
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "monsterHpRng");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class ResetQuestRngAfterCombatButBeforeRewardGenerationPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void reset() {
            // When loading a save, the patch above for reloading handles it
            if (!AbstractDungeon.loading_post_combat) {
                AbstractQuest.rng = new Random(Settings.seed + AbstractDungeon.floorNum + POST_COMBAT_SEED);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractRoom.class, "endBattleTimer");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
