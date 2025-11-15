package spireQuests.quests.example;

import com.megacrit.cardcrawl.rooms.EventRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class EnterRoomTestQuest extends AbstractQuest {
    public EnterRoomTestQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        new TriggerTracker<>(QuestTriggers.ENTER_ROOM, 5)
            .triggerCondition((node) -> node.room instanceof EventRoom)
            .add(this);

        addReward(new QuestReward.GoldReward(100));
    }
}
