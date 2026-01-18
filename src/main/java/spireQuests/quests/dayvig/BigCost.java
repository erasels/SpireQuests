package spireQuests.quests.dayvig;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class BigCost extends AbstractQuest {
    public BigCost() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        new TriggerTracker<>(QuestTriggers.ADD_CARD, 3)
                .triggerCondition(this::isBigCost)
                .add(this);

        addReward(new QuestReward.GoldReward(50));
    }

    public boolean isBigCost(AbstractCard card) {
        return card.cost >= 2;
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum < 3;
    }
}
