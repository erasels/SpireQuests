package spireQuests.quests.theabest;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class BolsterQuest extends AbstractQuest {

    public BolsterQuest() {
        super(QuestType.LONG, QuestDifficulty.EASY);
        new TriggerTracker<Integer>(QuestTriggers.MAX_HEALTH_INCREASED, 1)
                .add(this);
        addReward(new QuestReward.GoldReward(40));
    }
}
