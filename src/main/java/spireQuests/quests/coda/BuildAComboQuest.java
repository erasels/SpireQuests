package spireQuests.quests.coda;

import java.util.ArrayList;
import java.util.function.Function;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.util.Wiz;

public class BuildAComboQuest extends AbstractQuest {

    public BuildAComboQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);
        
        new TriggerTracker<AbstractCard>(QuestTriggers.PLAY_CARD, 1){
                @Override
                public String progressString(){
                    return "";
                }
            }.triggerCondition(this.isCost(1))
            .setResetTrigger(QuestTriggers.TURN_START, t -> true, false)
            .setResetTrigger(QuestTriggers.COMBAT_END, t -> true, false)
            .add(this);
        new TriggerTracker<AbstractCard>(QuestTriggers.PLAY_CARD, 1){
                @Override
                public String progressString(){
                    return "";
                }
            }.triggerCondition(this.isCost(2))
            .setResetTrigger(QuestTriggers.TURN_START, t -> true, false)
            .setResetTrigger(QuestTriggers.COMBAT_END, t -> true, false)
            .add(this);
        new TriggerTracker<AbstractCard>(QuestTriggers.PLAY_CARD, 1){
                @Override
                public String progressString(){
                    return "";
                }
            }.triggerCondition(this.isCost(3))
            .setResetTrigger(QuestTriggers.TURN_START, t -> true, false)
            .setResetTrigger(QuestTriggers.COMBAT_END, t -> true, false)
            .add(this);
        addGenericReward();
    }

    private Function<AbstractCard, Boolean> isCost(int cost) {
        return card -> card.cost != -1 && Wiz.getLogicalCardCost(card) == cost;
    }

    @Override
    public boolean canSpawn() {

        ArrayList<AbstractCard> deck = AbstractDungeon.player.masterDeck.group;
        
        boolean has1 = deck.stream().anyMatch(c -> c.cost == 1);
        boolean has2 = deck.stream().anyMatch(c -> c.cost == 2);
        boolean has3 = deck.stream().anyMatch(c -> c.cost == 3);
        
        return (has1 && has2 && has3);
    }
    
}
