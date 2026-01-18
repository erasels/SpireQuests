package spireQuests.quests.ramchops;

import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.Ectoplasm;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.ramchops.trackers.ClericFundsTracker;
import spireQuests.quests.ramchops.trackers.ClericRewardTracker;

import java.util.List;

import static spireQuests.util.Wiz.adp;

public class CharityQuest extends AbstractQuest implements CustomSavable<Integer>{

    int maxHPGain = 0;

    public CharityQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        useDefaultReward = false;

        new ClericFundsTracker().add(this);
        new ClericRewardTracker().add(this);
        new TriggerTracker<Integer>(QuestTriggers.GAIN_MONEY, 1){
            @Override
            public boolean isComplete() {
                return super.isComplete() || adp().hasRelic(Ectoplasm.ID);
            }
        }.add(this);

        needHoverTip = true;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onComplete() {
        questRewards.clear();

        if (maxHPGain <= 0) maxHPGain = 0;
        addReward(new QuestReward.MaxHPReward(maxHPGain));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum >= 1 && AbstractDungeon.actNum <= 2 && !adp().hasRelic(Ectoplasm.ID);
    }

    @Override
    public boolean complete() {

        if(questConditionsAreFulfilled() && maxHPGain == 0){
            Object o = trackers.get(1);

            if(o instanceof ClericRewardTracker){
                maxHPGain = ((ClericRewardTracker) o).localCount;
                if (maxHPGain == 0){
                    maxHPGain = -1;
                }
            }else{
                Anniv8Mod.logger.warn("Failed to detect ClericRewardTracker in the list of trackers. Please tell Ram to fix the code.");
            }
        }

        return super.complete();
    }

    @Override
    public Integer onSave() {
        return maxHPGain;
    }

    @Override
    public void onLoad(Integer integer) {
        maxHPGain = integer;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);

        Ectoplasm ecto = new Ectoplasm();

        tipList.add(new PowerTip(ecto.name, ecto.description));
    }
}

