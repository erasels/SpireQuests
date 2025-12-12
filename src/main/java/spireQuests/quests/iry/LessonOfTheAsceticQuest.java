package spireQuests.quests.iry;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Blasphemy;
import com.megacrit.cardcrawl.cards.red.Offering;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.iry.cards.ClumsyBlasphemy;
import spireQuests.quests.iry.cards.ClumsyOffering;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// LessonOfTheAsceticQuest:
// Obtain a modified blasphemy with ethereal and no upsides
// play it 3 times to receive a blasphemy+
public class LessonOfTheAsceticQuest extends AbstractQuest {

    public LessonOfTheAsceticQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 3)
            .triggerCondition((card) -> Objects.equals(card.cardID, ClumsyBlasphemy.ID))
            .add(this);

        questboundCards = new ArrayList<>();
        questboundCards.add(new ClumsyBlasphemy());

        AbstractCard rewardCard = new Blasphemy();
        rewardCard.upgrade();
        addReward(new QuestReward.CardReward(rewardCard));
        titleScale = 0.9f;

        useDefaultReward = false;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new ClumsyBlasphemy()));
    }

    @Override
    public boolean canSpawn() {
        return !Wiz.p().chosenClass.name().equals("WATCHER");
    }
}
