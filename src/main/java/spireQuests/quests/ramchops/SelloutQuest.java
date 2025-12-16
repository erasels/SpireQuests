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
import spireQuests.quests.ramchops.cards.SelloutAdvertisementCard;
import spireQuests.quests.ramchops.trackers.AdsPlayedQuestTracker;

import java.util.List;

public class SelloutQuest extends AbstractQuest implements CustomSavable<Integer>{

    int adRevenue = 0;

    public SelloutQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);

        useDefaultReward = false;

        new AdsPlayedQuestTracker().add(this);
        new TriggerTracker<>(QuestTriggers.REMOVE_CARD, 1).triggerCondition((card)->
           card instanceof SelloutAdvertisementCard
        ).add(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        AbstractCard selloutCard = new SelloutAdvertisementCard();
        AbstractDungeon.topLevelEffectsQueue.add(new ShowCardAndObtainEffect(selloutCard, (float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F, false));
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

        if(!this.isCompleted()) {
            Object o = trackers.get(0);

            if (o instanceof AdsPlayedQuestTracker) {
                adRevenue = ((AdsPlayedQuestTracker) o).localCount;
            } else {
                Anniv8Mod.logger.warn("Failed to detect AdsPlayedQuestTracker in the list of trackers. Please tell Ram to fix the code.");
            }
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

