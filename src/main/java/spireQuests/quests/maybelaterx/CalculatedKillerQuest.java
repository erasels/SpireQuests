package spireQuests.quests.maybelaterx;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.maybelaterx.relics.BalancingStones;

public class CalculatedKillerQuest extends AbstractQuest {

    public CalculatedKillerQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        new TriggerTracker<>(QuestTriggers.EXACT_KILL, 3)
                .add(this);
        addReward(new QuestReward.RelicReward(new BalancingStones()));
    }
}
