package spireQuests.quests.modargo;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.EventRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.relics.OwlMask;

public class ChartTheUnknownQuest extends AbstractQuest {
    public ChartTheUnknownQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        new TriggerTracker<>(QuestTriggers.ENTER_ROOM, 3)
                .triggerCondition((node) -> node.room instanceof EventRoom)
                .add(this);

        addReward(new QuestReward.RelicReward(new OwlMask()));
        titleScale = 0.9f;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1;
    }
}
