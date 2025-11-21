package spireQuests.quests.gk;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.potions.AttackPotion;
import com.megacrit.cardcrawl.potions.StrengthPotion;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class RagerQuest extends AbstractQuest {
    public RagerQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 5)
                .triggerCondition((card) -> card.type == AbstractCard.CardType.ATTACK)
                .setResetTrigger(QuestTriggers.TURN_START)
                .setResetTrigger(QuestTriggers.COMBAT_END)
                .add(this);

        addReward(new QuestReward.PotionReward(new AttackPotion()));
        addReward(new QuestReward.PotionReward(new StrengthPotion()));
    }
}
