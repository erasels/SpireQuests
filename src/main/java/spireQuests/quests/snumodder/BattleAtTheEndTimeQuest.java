package spireQuests.quests.snumodder;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.potions.PotionSlot;
import com.megacrit.cardcrawl.relics.Sozu;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.snumodder.potions.TickAndTockPotion;

public class BattleAtTheEndTimeQuest extends AbstractQuest {
    private int phase = 0;
    public BattleAtTheEndTimeQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        new TriggeredUpdateTracker<Integer, Integer>(QuestTriggers.POTION_CHANGE, 0, 1, () -> checkPhase() > 0 ? 1 : 0
        ){
            @Override
            public String saveData() {
                return state + ":" + phase;
            }

            @Override
            public void loadData(String data) {
                String[] split = data.split(":");
                phase = Integer.parseInt(split[1]);
                super.loadData(split[0]);
            }

            @Override
            public String progressString() {
                return String.format(
                        " (%d/%d)",
                        checkPhase() > 0 ? 0 : countPotions(), checkPhase() > 0 ? 0 : countPotions());
            }
        }.add(this);

        new TriggeredUpdateTracker<Integer, Integer>(QuestTriggers.POTION_CHANGE, 0, 1, () -> checkPhase() > 1 ? 1 : 0){
            @Override
            public String progressString() {
                return String.format(
                        " (%d/%d)",
                        checkPhase() > 1 ? 2 : countPotions(), 2);
            }
        }.add(this);

        new TriggeredUpdateTracker<Integer, Integer>(QuestTriggers.POTION_CHANGE, 0, 1, () -> checkPhase() > 2 ? 1 : 0){
            @Override
            public String progressString() {
                return String.format(
                        " (%d/%d)",
                        checkPhase() > 2 ? 0 : countPotions(), checkPhase() > 2 ? 0 : countPotions());
            }
        }.add(this);

        addReward(new QuestReward.PotionReward(new TickAndTockPotion()));
        titleScale = 0.8f;
    }

    public static int countPotions() {
        int count = 0;
        for (AbstractPotion p : AbstractDungeon.player.potions) {
            if (!(p instanceof PotionSlot)) {
                count++;
            }
        }
        return count;
    }

    public int checkPhase() {
        int count = countPotions();
        switch (phase) {
            case 0:
                if (count == 0) phase = 1;
                break;
            case 1:
                if (count > 1) phase = 2;
                break;
            case 2:
                if (count == 0) phase = 3;
        }
        return phase;
    }

    @Override
    public boolean canSpawn() {
        return !AbstractDungeon.player.hasRelic(Sozu.ID);
    }
}
