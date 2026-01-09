package spireQuests.quests.modargo;

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
import spireQuests.quests.modargo.monsters.DefectEliteMonster;
import spireQuests.quests.modargo.relics.VolatileStardust;
import spireQuests.util.NodeUtil;

import static spireQuests.Anniv8Mod.makeID;

public class BountyDefectQuest extends AbstractQuest {
    private static final String ID = makeID(BountyDefectQuest.class.getSimpleName());

    public BountyDefectQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.VICTORY, 1)
                .triggerCondition((x) -> AbstractDungeon.getCurrRoom().eliteTrigger &&
                        DefectEliteMonster.ID.equals(AbstractDungeon.lastCombatMetricKey))
                .setFailureTrigger(QuestTriggers.ACT_CHANGE)
                .add(this);

        addReward(new QuestReward.RelicReward(new VolatileStardust()));
        titleScale = 0.9f;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 3 && NodeUtil.canPathToElite();
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getEliteMonsterForRoomCreation")
    public static class SpawnElite {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> replacementPatch() {
            // if this quest exists
            BountyDefectQuest q = (BountyDefectQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                Anniv8Mod.logger.info("Replacing ELITE with Defect");
                AbstractDungeon.lastCombatMetricKey = DefectEliteMonster.ID;
                return SpireReturn.Return(new MonsterGroup(new DefectEliteMonster()));
            }
            return SpireReturn.Continue();
        }
    }
}
