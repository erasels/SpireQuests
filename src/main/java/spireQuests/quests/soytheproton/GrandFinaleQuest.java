package spireQuests.quests.soytheproton;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.green.GrandFinale;
import com.megacrit.cardcrawl.characters.TheSilent;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GrandFinaleQuest extends AbstractQuest {
    public GrandFinaleQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 1)
                .triggerCondition((card) -> Objects.equals(card.cardID, GrandFinale.ID))
                .add(this);
        addReward(new QuestReward.GoldReward(150));

        questboundCards = new ArrayList<>();
        questboundCards.add(new GrandFinale());
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new GrandFinale()));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.player instanceof TheSilent;
    }
}
