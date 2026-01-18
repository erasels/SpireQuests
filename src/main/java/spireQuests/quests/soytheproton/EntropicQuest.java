package spireQuests.quests.soytheproton;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;

import java.util.ArrayList;

import static spireQuests.quests.QuestGenerator.rollQuestForDifficulty;
import static spireQuests.quests.QuestGenerator.seenQuests;

public class EntropicQuest extends AbstractQuest {
    public EntropicQuest() {
        super(QuestType.LONG, QuestDifficulty.EASY);

        new TriggerTracker<>(QuestTriggers.QUEST_COMPLETE, 1)
                .triggerCondition((x) -> QuestManager.quests().size() == 1)
                .add(this);

    }

    @Override
    public void onComplete() {
        ArrayList<AbstractQuest> generatedQuests = new ArrayList<>();
        Random rng = new Random(Settings.seed + (9419L * (AbstractDungeon.floorNum + 1)));

        for(AbstractQuest.QuestDifficulty difficulty : new QuestDifficulty[]{QuestDifficulty.EASY, QuestDifficulty.NORMAL, QuestDifficulty.HARD}) {
            AbstractQuest quest = rollQuestForDifficulty(difficulty, seenQuests.get(AbstractDungeon.player), rng);
            if (quest != null) {
                quest.setCost();
                generatedQuests.add(quest);
                seenQuests.get(AbstractDungeon.player).add(quest.id);
            }
        }

        for(AbstractQuest q : generatedQuests) {
            q.needHoverTip=true;
            QuestManager.startQuest(q);
        }

    }
    @Override
    public boolean canSpawn() {
        return QuestManager.quests().size() > 1;
    }
}
