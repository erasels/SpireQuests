package spireQuests.quests.coda;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.util.Wiz;

public class UntouchableQuest extends AbstractQuest {

    public UntouchableQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.COMBAT_END, 3)
            .triggerCondition((x) -> AbstractDungeon.player.damagedThisCombat <= 0)
            .setResetTrigger(QuestTriggers.DAMAGE_TAKEN, (x) -> Wiz.isInCombat())
            .add(this);

        addGenericReward();
    }
    
}
