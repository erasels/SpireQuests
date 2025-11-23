package spireQuests.quests.indi_keurodz;

import com.megacrit.cardcrawl.relics.BurningBlood;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;

public class BalatroQuest extends AbstractQuest {
    public static int BLIND_FIGHTS_COMPLETED = 0;

    public BalatroQuest() {
        super(QuestType.LONG, QuestDifficulty.CHALLENGE);

        new TriggeredUpdateTracker<>(QuestTriggers.VICTORY, 0, 10, () -> {
            return BLIND_FIGHTS_COMPLETED;
        }).add(this);

        addReward(new QuestReward.RelicReward(new BurningBlood()));
        needHoverTip = true;
    }

    @Override
    public void onStart() {
        super.onStart();

        StickersInRewardsPatch.ENABLED = true;
    }

    @Override
    public void loadSave(String[] questData, QuestReward.QuestRewardSave[] questRewardSaves) {
        super.loadSave(questData, questRewardSaves);

        StickersInRewardsPatch.ENABLED = true;
    }

}
