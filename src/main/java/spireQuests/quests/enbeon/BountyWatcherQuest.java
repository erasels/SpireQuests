package spireQuests.quests.enbeon;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.enbeon.monsters.WatcherEliteMonster;
import spireQuests.quests.enbeon.relics.DivineOculus;
import spireQuests.util.NodeUtil;

import static spireQuests.Anniv8Mod.makeID;

public class BountyWatcherQuest extends AbstractQuest {
    private static final String ID = makeID(BountyWatcherQuest.class.getSimpleName());

    public BountyWatcherQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.VICTORY, 1)
                .triggerCondition((x) -> AbstractDungeon.getCurrRoom().eliteTrigger &&
                        WatcherEliteMonster.ID.equals(AbstractDungeon.lastCombatMetricKey))
                .setFailureTrigger(QuestTriggers.ACT_CHANGE)
                .add(this);

        addReward(new QuestReward.RelicReward(new DivineOculus()));
        titleScale = 0.9f;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 2 && NodeUtil.canPathToElite();
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getEliteMonsterForRoomCreation")
    public static class SpawnElite {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> replacementPatch() {
            // if this quest exists
            BountyWatcherQuest q = (BountyWatcherQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                Anniv8Mod.logger.info("Replacing ELITE with Watcher");
                AbstractDungeon.lastCombatMetricKey = WatcherEliteMonster.ID;
                return SpireReturn.Return(new MonsterGroup(new WatcherEliteMonster()));
            }
            return SpireReturn.Continue();
        }
    }
}
