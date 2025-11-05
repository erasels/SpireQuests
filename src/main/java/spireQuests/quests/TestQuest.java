package spireQuests.quests;

import com.megacrit.cardcrawl.cards.AbstractCard;
import spireQuests.patches.QuestTriggers;

import static spireQuests.Anniv8Mod.makeID;

public class TestQuest extends AbstractQuest {
    public static final String ID = makeID("TESTQUEST");
    public TestQuest() {
        super(ID, QuestType.LONG, QuestDifficulty.HARD);

        addTracker(new TriggerTracker<>(QuestTriggers.ADD_CARD, 5)
                .triggerCondition((card)->card.rarity == AbstractCard.CardRarity.COMMON)
                .setResetTrigger(QuestTriggers.ADD_CARD, (card)->card.rarity != AbstractCard.CardRarity.COMMON));
    }
}
