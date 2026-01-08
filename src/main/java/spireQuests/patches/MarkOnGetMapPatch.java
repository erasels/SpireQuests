package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import spireQuests.quests.MarkNodeQuest;
import spireQuests.quests.QuestManager;

public class MarkOnGetMapPatch {
    /**
     * Field to track if the marking process has already been done for the act
     */
    @SpirePatch2(clz = AbstractDungeon.class, method = SpirePatch.CLASS)
    public static class MarkedField {
        public static SpireField<Boolean> marked = new SpireField<>(() -> false);
    }

    /**
     * Patches for loading markings at a new act, and after saving and resuming a run.
     */
    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class})
    @SpirePatch2(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class, SaveFile.class})
    public static class MarkNodesOnGetDungeonPatch {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeon(CardCrawlGame __instance) {
            if (!Loader.isModLoaded("actlikeit") && !MarkedField.marked.get(CardCrawlGame.dungeon)) {
                for(Object q : QuestManager.quests().stream().filter(q -> q instanceof MarkNodeQuest).toArray()){
                    MarkNodeQuest quest = ((MarkNodeQuest) q);
                    quest.markNodes(AbstractDungeon.map, quest.rng());
                }
                MarkedField.marked.set(CardCrawlGame.dungeon, true);
            }
        }
    }

    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughProgression", method = "Postfix", paramtypez = { AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class }, requiredModId = "actlikeit")
    @SpirePatch2(cls = "actlikeit.patches.GetDungeonPatches$getDungeonThroughSavefile", method = "Postfix", paramtypez = { AbstractDungeon.class, CardCrawlGame.class, String.class, AbstractPlayer.class, SaveFile.class }, requiredModId = "actlikeit")
    public static class MarkNodesOnGetDungeonActLikeIt {
        @SpirePostfixPatch
        public static void markNodesOnGetDungeonActLikeIt() {
            if (!MarkedField.marked.get(CardCrawlGame.dungeon)) {
                for (Object q : QuestManager.quests().stream().filter(q -> q instanceof MarkNodeQuest).toArray()) {
                    MarkNodeQuest quest = ((MarkNodeQuest) q);
                    quest.markNodes(AbstractDungeon.map, quest.rng());
                }
                MarkedField.marked.set(CardCrawlGame.dungeon, true);
            }
        }
    }

    // When loading a save file, populatePathTaken calls nextRoomTransition, which trigger ENTER_ROOM for quests.
    // We need the markings to be on the map before that.
    // In theory, now that this patch exists we might be able to get rid of the SaveFile versions of the other patches.
    // However, we've left them in place in case there are code paths that still need them, and because we check whether
    // the marking has already been done so it should be safe
    @SpirePatch2(clz = AbstractDungeon.class, method = "populatePathTaken")
    public static class MarkNodesBeforePopulatePathTaken {
        @SpirePrefixPatch
        public static void markNodesBeforePopulatePathTaken() {
            if (!MarkedField.marked.get(CardCrawlGame.dungeon)) {
                for (Object q : QuestManager.quests().stream().filter(q -> q instanceof MarkNodeQuest).toArray()) {
                    MarkNodeQuest quest = ((MarkNodeQuest) q);
                    quest.markNodes(AbstractDungeon.map, quest.rng());
                }
                MarkedField.marked.set(CardCrawlGame.dungeon, true);
            }
        }
    }
}
