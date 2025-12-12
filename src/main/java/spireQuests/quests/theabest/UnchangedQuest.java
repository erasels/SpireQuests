package spireQuests.quests.theabest;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class UnchangedQuest extends AbstractQuest {
    public UnchangedQuest() {
        super(QuestType.LONG, QuestDifficulty.HARD);
        new TriggerTracker<>(QuestTriggers.COMBAT_END, 1)
                .triggerCondition((x) -> AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss && AbstractDungeon.actNum == 2)
                .setFailureTrigger(QuestTriggers.MAX_HEALTH_CHANGED)
                .add(this);
        addReward(new QuestReward.GoldReward(150));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1;
    }
}
