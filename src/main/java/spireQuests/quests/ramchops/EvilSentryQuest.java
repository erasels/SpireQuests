package spireQuests.quests.ramchops;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.quests.ramchops.monsters.EvilSentry;
import spireQuests.quests.ramchops.relics.FriendSentry;
import spireQuests.util.NodeUtil;

import static spireQuests.Anniv8Mod.makeID;

public class EvilSentryQuest extends AbstractQuest {

    private static final String ID = makeID(EvilSentryQuest.class.getSimpleName());

    public EvilSentryQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);
        addReward(new QuestReward.RelicReward(new FriendSentry()));

        new TriggerTracker<>(QuestTriggers.VICTORY, 1)
                .triggerCondition((x) -> AbstractDungeon.getCurrRoom().eliteTrigger &&
                        EvilSentry.ID.equals(AbstractDungeon.lastCombatMetricKey))
                .setFailureTrigger(QuestTriggers.ACT_CHANGE)
                .add(this);
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1 && NodeUtil.canPathToElite();
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getEliteMonsterForRoomCreation")
    public static class SpawnElite {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> replacementPatch() {
            // if this quest exists
            EvilSentryQuest q = (EvilSentryQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                Anniv8Mod.logger.info("Replacing ELITE with Evil Sentries");
                AbstractDungeon.lastCombatMetricKey = EvilSentry.ID;
                return SpireReturn.Return(new MonsterGroup(new AbstractMonster[]{
                        new EvilSentry(-330.0F, 25.0F),
                        new EvilSentry(-85.0F, 10.0F),
                        new EvilSentry(140.0F, 30.0F)
                }));
            }
            return SpireReturn.Continue();
        }
    }
}
