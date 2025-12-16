package spireQuests.quests.iry;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Offering;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.random.Random;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.iry.cards.ClumsyOffering;
import spireQuests.quests.iry.util.LessonQuestUtil;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// LessonOfTheBloodQuest:
// Obtain a modified offering with ethereal and no upsides
// play it 4 times to receive an offering+
public class LessonOfTheBloodQuest extends AbstractQuest {

    public LessonOfTheBloodQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 4)
            .triggerCondition((card) -> Objects.equals(card.cardID, ClumsyOffering.ID))
            .add(this);

        questboundCards = new ArrayList<>();
        questboundCards.add(new ClumsyOffering());

        AbstractCard offering = new Offering();
        offering.upgrade();
        addReward(new QuestReward.CardReward(offering));
        titleScale = 0.9f;

        useDefaultReward = false;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new ClumsyOffering()));
    }

    @Override
    public boolean canSpawn() {
        Random rng = new Random(Settings.seed + (1911L * (AbstractDungeon.floorNum + 1)));
        boolean weightedSpawnCheck = rng.randomBoolean(LessonQuestUtil.getLessonSpawnChance());
        return weightedSpawnCheck && !Wiz.p().chosenClass.name().equals("IRONCLAD");
    }
}
