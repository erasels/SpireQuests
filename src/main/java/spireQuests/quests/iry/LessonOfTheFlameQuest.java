package spireQuests.quests.iry;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.FiendFire;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.iry.cards.ClumsyFiendFire;
import spireQuests.quests.iry.util.LessonQuestUtil;
import spireQuests.util.Wiz;

import java.util.ArrayList;
import java.util.Objects;

// LessonOfTheFlameQuest:
// Obtain a modified fiend fire with ethereal and no upsides
// play it 4 times to receive a fiend fire+
public class LessonOfTheFlameQuest extends AbstractQuest {

    public LessonOfTheFlameQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.PLAY_CARD, 4)
            .triggerCondition((card) -> Objects.equals(card.cardID, ClumsyFiendFire.ID))
            .add(this);

        questboundCards = new ArrayList<>();
        questboundCards.add(new ClumsyFiendFire());

        AbstractCard fiendFire = new FiendFire();
        fiendFire.upgrade();
        addReward(new QuestReward.CardReward(fiendFire));
        titleScale = 0.9f;

        useDefaultReward = false;
    }

    @Override
    public boolean canSpawn() {
        Random rng = new Random(Settings.seed + (1911L * (AbstractDungeon.floorNum + 1)));
        boolean weightedSpawnCheck = rng.randomBoolean(LessonQuestUtil.getLessonSpawnChance());
        return weightedSpawnCheck && !Wiz.p().chosenClass.name().equals("IRONCLAD");
    }
}
