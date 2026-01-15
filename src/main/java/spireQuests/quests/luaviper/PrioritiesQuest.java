package spireQuests.quests.luaviper;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.city.Taskmaster;
import com.megacrit.cardcrawl.monsters.exordium.SlaverBlue;
import com.megacrit.cardcrawl.monsters.exordium.SlaverRed;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.util.NodeUtil;

import java.util.Objects;

import static spireQuests.Anniv8Mod.makeID;

public class PrioritiesQuest extends AbstractQuest {

    private static final String ID = makeID(PrioritiesQuest.class.getSimpleName());
    private static final String TASKMASTER_COMBAT_ID = "Slavers";

    public PrioritiesQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        titleScale = 1.0f;
        isAutoComplete = true;
        useDefaultReward = false;

        new TriggerTracker<>(QuestTriggers.ENEMY_DIES_DELAYED_CHECK, 1)
                .triggerCondition((m) -> Objects.equals(m.id, SlaverBlue.ID))
                .setFailureTrigger(QuestTriggers.ENEMY_DIES_DELAYED_CHECK, (m) -> !checkObjectiveCompletionOrder(m))
                .add(this);
        new TriggerTracker<>(QuestTriggers.ENEMY_DIES_DELAYED_CHECK, 1)
                .triggerCondition((m) -> Objects.equals(m.id, Taskmaster.ID))
                .setFailureTrigger(QuestTriggers.ENEMY_DIES_DELAYED_CHECK, (m) -> !checkObjectiveCompletionOrder(m))
                .add(this);
        new TriggerTracker<>(QuestTriggers.ENEMY_DIES_DELAYED_CHECK, 1)
                .triggerCondition((m) -> Objects.equals(m.id, SlaverRed.ID))
                .setFailureTrigger(QuestTriggers.ENEMY_DIES_DELAYED_CHECK, (m) -> !checkObjectiveCompletionOrder(m))
                .setFailureTrigger(QuestTriggers.COMBAT_END, (v) -> Objects.equals(AbstractDungeon.lastCombatMetricKey, TASKMASTER_COMBAT_ID))
                .setFailureTrigger(QuestTriggers.ACT_CHANGE)
                .add(this);

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkObjectiveCompletionOrder(AbstractMonster m) {
        //note that ENEMY_DIES_DELAYED_CHECK will trigger on any enemy in any combat on any floor, so make sure we're on the right floor first...
        if (!Objects.equals(AbstractDungeon.lastCombatMetricKey, TASKMASTER_COMBAT_ID))
            return true;

        int targetTracker = -1;
        if (Objects.equals(m.id, SlaverBlue.ID)) {
            targetTracker = 0;
            if (isMonsterDead(Taskmaster.ID)) return false;
            if (isMonsterDead(SlaverRed.ID)) return false;
        }
        if (Objects.equals(m.id, Taskmaster.ID)) {
            targetTracker = 1;
            if (isMonsterDead(SlaverRed.ID)) return false;
        }
        if (Objects.equals(m.id, SlaverRed.ID)) {
            targetTracker = 2;
        }
        for (int i = 0; i < trackers.size(); i += 1) {
            if (i < targetTracker)
                if (!trackers.get(i).isComplete())
                    return false;
        }

        return true;
    }

    private boolean isMonsterDead(String id) {
        AbstractMonster m = AbstractDungeon.getMonsters().getMonster(id);
        return m != null && m.isDeadOrEscaped();
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
            PrioritiesQuest q = (PrioritiesQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if (q != null) {
                Anniv8Mod.logger.info("Replacing ELITE with Taskmaster");
                AbstractDungeon.lastCombatMetricKey = TASKMASTER_COMBAT_ID;
                return SpireReturn.Return(new MonsterGroup(new AbstractMonster[]{
                        new SlaverBlue(-385.0F, -15.0F),
                        new Taskmaster(-133.0F, 0.0F),
                        new SlaverRed(125.0F, -30.0F)
                }));
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = CombatRewardScreen.class, method = "open", paramtypez = {})
    public static class UpgradeAllRewards {
        @SpirePostfixPatch
        public static void Postfix(CombatRewardScreen __instance) {
            // if this quest exists and is completed
            PrioritiesQuest q = (PrioritiesQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && quest.isCompleted())
                    .findAny()
                    .orElse(null);
            if (q != null) {
                for (RewardItem r : __instance.rewards) {
                    if (r.cards != null) {
                        for (AbstractCard card : r.cards) {
                            if(card.canUpgrade()) {
                                //Anniv8Mod.logger.info("Upgrading " + card.name);
                                card.upgrade();
                            }
                        }
                    }
                }
            }
        }
    }
}