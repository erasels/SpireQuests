package spireQuests.quests.ramchops;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.OptionalTriggerTracker;

public class SelloutCombatTracker extends OptionalTriggerTracker<Void> {

    public SelloutCombatTracker(){
        super(QuestTriggers.VICTORY, 5, false);
    }


    @Override
    public void trigger(Void param) {
        super.trigger(param);
        localCount += 1;

        if (localCount == localGoal){
            localCount = 0;
            AbstractCard ad = AbstractDungeon.player.masterDeck.findCardById(SelloutAdvertisementCard.ID);
            if (ad != null) {
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(ad));
                CardCrawlGame.sound.play("CARD_EXHAUST");
                AbstractDungeon.player.masterDeck.removeCard(ad);
            }
        }
    }
}
