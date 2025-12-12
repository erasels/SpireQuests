package spireQuests.quests.iry;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.Nightmare;
import com.megacrit.cardcrawl.cards.red.Offering;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.iry.cards.ClumsyNightmare;
import spireQuests.quests.iry.cards.ClumsyOffering;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// LessonOfTheBogQuest:
// Obtain a modified nightmare with ethereal and no upsides
// play it 4 times to receive a nightmare+
public class LessonOfTheBogQuest extends AbstractQuest {

    public LessonOfTheBogQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 4)
            .triggerCondition((card) -> Objects.equals(card.cardID, ClumsyNightmare.ID))
            .add(this);

        questboundCards = new ArrayList<>();
        questboundCards.add(new ClumsyNightmare());

        AbstractCard nightmare = new Nightmare();
        nightmare.upgrade();
        addReward(new QuestReward.CardReward(nightmare));
        titleScale = 0.9f;

        useDefaultReward = false;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new ClumsyNightmare()));
    }

    @Override
    public boolean canSpawn() {
        return !Wiz.p().chosenClass.name().equals("THE_SILENT");
    }
}
