package spireQuests.quests.iry;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.purple.Blasphemy;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.random.Random;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.iry.cards.ClumsyBlasphemy;
import spireQuests.quests.iry.util.LessonQuestUtil;
import spireQuests.util.Wiz;
import java.util.*;

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
    public boolean canSpawn() {
        Random rng = new Random(Settings.seed + (1914L * (AbstractDungeon.floorNum + 1)));
        boolean weightedSpawnCheck = rng.randomBoolean(LessonQuestUtil.getLessonSpawnChance());
        return weightedSpawnCheck && !Wiz.p().chosenClass.name().equals("WATCHER");
    }
}
