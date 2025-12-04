package spireQuests.quests.ramchops;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MembershipCard;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class WindowShoppingQuest extends AbstractQuest {

    private final int SHOP_VISITS = 2;

    public WindowShoppingQuest() {
        super(QuestType.LONG, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.LEAVE_ROOM, SHOP_VISITS)
                .triggerCondition((mapRoomNode) ->
                    mapRoomNode.room instanceof ShopRoom
                ).setFailureTrigger(QuestTriggers.MONEY_SPENT_AT_SHOP)
                .add(this);

        addReward(new QuestReward.RelicReward(new MembershipCard()));
        titleScale = 0.9f;
    }


    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum >= 1 && AbstractDungeon.actNum <= 2;
    }
}
