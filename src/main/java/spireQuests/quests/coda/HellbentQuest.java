package spireQuests.quests.coda;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

public class HellbentQuest extends AbstractQuest {

    public HellbentQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.TURN_END, 5)
            .triggerCondition((x) -> AbstractDungeon.player.hand.isEmpty())
            .add(this);

        addGenericReward();
    }
    
}
