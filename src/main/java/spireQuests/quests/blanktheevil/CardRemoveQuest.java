package spireQuests.quests.blanktheevil;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CardRemoveQuest extends AbstractQuest {
    private static final QuestType QUEST_TYPE = QuestType.SHORT;
    private static final QuestDifficulty QUEST_DIFFICULTY = QuestDifficulty.EASY;

    public CardRemoveQuest() {
        super(QUEST_TYPE, QUEST_DIFFICULTY);

        new TriggerTracker<>(QuestTriggers.REMOVE_CARD, 5)
                .triggerCondition(card -> card.rarity == AbstractCard.CardRarity.BASIC)
                .add(this);
    }

    @Override
    public void onComplete() {
        Set<AbstractCard> cards = new HashSet<>();

        while (cards.size() < 3) {
            cards.add(AbstractDungeon.getCard(AbstractCard.CardRarity.RARE));
        }

        AbstractDungeon.previousScreen = AbstractDungeon.screen;
        AbstractDungeon.cardRewardScreen.open(new ArrayList<>(cards), null, "Choose a card.");
    }
}
