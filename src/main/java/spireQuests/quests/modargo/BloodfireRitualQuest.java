package spireQuests.quests.modargo;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.modargo.cards.bloodfire.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BloodfireRitualQuest extends AbstractQuest {
    public BloodfireRitualQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);
        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 20)
                .triggerCondition(card -> card instanceof BloodfireRitualCard)
                .add(this);

        addReward(new QuestReward.CardReward(new TheBlackstaff()));

        questboundCards = new ArrayList<>();
        questboundCards.add(new BloodfireRitualQuestboundPlaceholder());
    }

    @Override
    public ArrayList<AbstractCard> overrideQuestboundCards() {
        List<AbstractCard> cards = Arrays.asList(new FocusedTrance(), new BloodInTheChalice(), new PrimordialFlux(), new EssenceOfFlame(), new PowerFromTheDark());
        AbstractCard card = cards.get(AbstractDungeon.cardRandomRng.random(cards.size() - 1));
        return new ArrayList<>(Collections.singleton(card));
    }
}
