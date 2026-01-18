package spireQuests.quests.soytheproton;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.soytheproton.relics.IoMoth;

import java.util.List;

import static spireQuests.Anniv8Mod.makeID;

public class MothQuest extends AbstractQuest {
    public static final String FLY_SFX = makeID("FlySfx");
    public static final String MOTH_SFX = makeID("MothSfx");

    public MothQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);

        new TriggerTracker<>(QuestTriggers.ACT_CHANGE,1)
                .add(this);
        new TriggeredUpdateTracker<Integer, Integer>(QuestTriggers.UNBLOCKED_ATTACK_DAMAGE_TAKEN,
                0, 25, Integer::sum, ()->false) {

            @Override
            public boolean isComplete() {
                return !isFailed();
            }
            @Override
            public boolean isFailed() {
                return (condition == null || condition.get()) && state >= target;
            }
        }
                .add(this);
        addReward(new QuestReward.RelicReward(new IoMoth()));
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new PowerTip(questStrings.EXTRA_TEXT[0],questStrings.EXTRA_TEXT[1]));
    }

    @Override
    public boolean canSpawn() {
        // This will make it so it spawns on the first five floors of Act 1 and 2, assuming no shenanigans.
        return AbstractDungeon.floorNum <= 5 || AbstractDungeon.floorNum > 17 && AbstractDungeon.floorNum <= 23;
    }
}
