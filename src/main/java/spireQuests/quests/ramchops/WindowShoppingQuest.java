package spireQuests.quests.ramchops;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MembershipCard;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.ramchops.patch.ShopMoneyTracker;

import static spireQuests.util.Wiz.adp;
import static spireQuests.util.Wiz.curRoom;

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
    }

    @Override
    public void onStart() {
        super.onStart();

        if (ShopMoneyTracker.getMoneySpentInRoom(curRoom()) > 0){
            this.forceFail();
        }
        else {
            AbstractDungeon.shopRelicPool.remove(MembershipCard.ID);
        }
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum >= 1 && AbstractDungeon.actNum <= 2 && !adp().hasRelic(MembershipCard.ID);
    }

    @Override
    public String getDescription() {
        if(curRoom() instanceof ShopRoom){
            return super.getDescription() + questStrings.EXTRA_TEXT[0];
        }

        return super.getDescription();
    }
}
