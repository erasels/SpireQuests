package spireQuests.quests.example;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.Courier;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class LeaveRoomTestQuest extends AbstractQuest {
    public LeaveRoomTestQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.LEAVE_ROOM, 2)
                .triggerCondition((node) -> node.room instanceof ShopRoom && AbstractDungeon.player.gold <= 50)
                .add(this);

        addReward(new QuestReward.RelicReward(new Courier()));
    }
}
