package spireQuests.quests.ramchops;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.MembershipCard;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class WindowShoppingQuest extends AbstractQuest {

    private final int SHOP_VISITS = 3;

    public WindowShoppingQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        new TriggerTracker<>(QuestTriggers.LEAVE_ROOM, SHOP_VISITS)
                .setFailureTrigger(QuestTriggers.MONEY_SPENT_AT_SHOP)
                .add(this);

        addReward(new QuestReward.RelicReward(new MembershipCard()));
    }


    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum >= 1 && AbstractDungeon.actNum <= 2;
    }
}
