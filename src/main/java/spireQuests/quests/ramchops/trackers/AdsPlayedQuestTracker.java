package spireQuests.quests.ramchops.trackers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.OptionalTriggerTracker;
import spireQuests.quests.ramchops.cards.SelloutAdvertisementCard;


public class AdsPlayedQuestTracker extends OptionalTriggerTracker<AbstractCard> {

    static int AD_REVENUE = 10;

    public AdsPlayedQuestTracker(){
        super(QuestTriggers.PLAY_CARD, 0, true);
    }

    @Override
    public void trigger(AbstractCard param) {
        if(param instanceof SelloutAdvertisementCard){
            localCount += AD_REVENUE;
        }
    }
}
