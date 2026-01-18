package spireQuests.quests.iry;

import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.Reboot;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.random.Random;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.iry.cards.ClumsyReboot;
import spireQuests.quests.iry.util.LessonQuestUtil;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// LessonOfTheCoreQuest:
// Obtain a modified reboot with ethereal and no upsides
// play it 9 times to receive a reboot+
public class LessonOfTheCoreQuest extends AbstractQuest {

    public LessonOfTheCoreQuest() {
        super(QuestType.LONG, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 9)
            .triggerCondition((card) -> Objects.equals(card.cardID, ClumsyReboot.ID))
            .add(this);

        questboundCards = new ArrayList<>();
        questboundCards.add(new ClumsyReboot());

        AbstractCard reboot = new Reboot();
        reboot.upgrade();
        addReward(new QuestReward.CardReward(reboot));
        titleScale = 0.9f;

        useDefaultReward = false;
    }

    @Override
    public boolean canSpawn() {
        Random rng = new Random(Settings.seed + (1913L * (AbstractDungeon.floorNum + 1)));
        boolean weightedSpawnCheck = rng.randomBoolean(LessonQuestUtil.getLessonSpawnChance());
        return weightedSpawnCheck && !Wiz.p().chosenClass.name().equals("DEFECT") && (AbstractDungeon.actNum == 1 || AbstractDungeon.actNum == 2);
    }
}
