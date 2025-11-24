package spireQuests.quests.theabest;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.theabest.relics.GlassBar;

public class BolsterQuest extends AbstractQuest implements CustomSavable<Integer> {
    private int maxHealthGained = 0;

    public BolsterQuest() {
        super(QuestType.LONG, QuestDifficulty.HARD);
        new PassiveTracker<Integer>(() -> maxHealthGained, 25)
                .add(this);
        new TriggerEvent<Integer>(QuestTriggers.MAX_HEALTH_INCREASED, (amount) -> {
            maxHealthGained += amount;
        }).add(this);
        addReward(new QuestReward.GoldReward(100));
    }

    @Override
    public Integer onSave() {
        return maxHealthGained;
    }

    @Override
    public void onLoad(Integer integer) {
        maxHealthGained = integer;
    }
}
