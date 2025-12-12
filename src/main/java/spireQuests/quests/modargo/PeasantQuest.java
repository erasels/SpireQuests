package spireQuests.quests.modargo;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.relics.PeasantsTunic;

import java.util.ArrayList;

public class PeasantQuest extends AbstractQuest {
    public PeasantQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);
        new TriggeredUpdateTracker<>(QuestTriggers.DECK_CHANGE, 0, 8, () -> {
            ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
            int commons = (int) deck.stream().filter(c -> c.rarity == AbstractCard.CardRarity.COMMON).count();
            int others = (int) deck.stream().filter(c -> c.rarity == AbstractCard.CardRarity.UNCOMMON || c.rarity == AbstractCard.CardRarity.RARE).count();
            return Math.max(commons - others, 0);
        }).add(this);

        addReward(new QuestReward.RelicReward(new PeasantsTunic()));
        needHoverTip = true;
    }
}
