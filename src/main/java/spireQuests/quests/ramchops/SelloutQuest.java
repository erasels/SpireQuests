package spireQuests.quests.ramchops;

import basemod.abstracts.CustomSavable;
import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.Trigger;

import java.util.ArrayList;
import java.util.List;

public class SelloutQuest extends AbstractQuest implements CustomSavable<Integer>{

    boolean initialPickup = false;
    int adRevenue = 0;

    public SelloutQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        useDefaultReward = false;
        rewardsText = localization.EXTRA_TEXT[3];

        new AdsPlayedQuestTracker().add(this);
        new SelloutCombatTracker().add(this);
        new TriggerTracker<>(QuestTriggers.REMOVE_CARD, 3).triggerCondition((card)->
           card instanceof SelloutAdvertisementCard
        ).add(this);
        addReward(new QuestReward.GoldReward(0));
    }

    @Override
    public void onStart() {
        initialPickup = true;
        super.onStart();
        initialPickup = false;

        float count = 3.0f;
        float displayCount = count;

        for (int i = 0; i < count; i++){
            AbstractCard selloutCard = new SelloutAdvertisementCard();
            selloutCard.initializeDescription();

            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(selloutCard, (float) Settings.WIDTH / count + displayCount, (float)Settings.HEIGHT / 2.0F, false));
            displayCount += (float)(Settings.WIDTH / (6.0F)) * (3/count);
            displayCount++;
        }
    }

    @Override
    public void onComplete() {
        questRewards.clear();
        addReward(new QuestReward.GoldReward(adRevenue));
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum >= 1 && AbstractDungeon.actNum <= 2;
    }

    @Override
    public void makeTooltips(List<PowerTip> tipList) {
        super.makeTooltips(tipList);
        tipList.add(new CardPowerTip(new SelloutAdvertisementCard()));
    }

    @Override
    public boolean complete() {

        Object o = trackers.get(0);

        if(o instanceof AdsPlayedQuestTracker){
            adRevenue = ((AdsPlayedQuestTracker) o).localCount;
        }else{
            Anniv8Mod.logger.warn("Failed to detect AdsPlayedQuestTracker in the list of trackers. Please tell Ram to fix the code.");
        }

        return super.complete();
    }

    @Override
    public Integer onSave() {
        return adRevenue;
    }

    @Override
    public void onLoad(Integer integer) {
        adRevenue = integer;
    }
}

