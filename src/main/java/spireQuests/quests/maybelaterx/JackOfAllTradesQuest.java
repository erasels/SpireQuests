package spireQuests.quests.maybelaterx;

import com.megacrit.cardcrawl.cards.AbstractCard;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.maybelaterx.relics.BalancingStonesRelic;

public class JackOfAllTradesQuest extends AbstractQuest {

    public JackOfAllTradesQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        new TriggerTracker<>(QuestTriggers.ADD_CARD, 2)
                .triggerCondition((card -> card.color == AbstractCard.CardColor.COLORLESS))
                .add(this);

        addGenericReward();
    }
}
