package spireQuests.quests.gk;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.gk.relics.ProudBadge;

public class AbstinenceQuest extends AbstractQuest {
    private static final int COMBATS = 4;

    public AbstinenceQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        new TriggerTracker<>(QuestTriggers.VICTORY, COMBATS)
                .setFailureTrigger(QuestTriggers.USE_POTION)
                .add(this);

        addReward(new QuestReward.RelicReward(new ProudBadge()));
    }
}
