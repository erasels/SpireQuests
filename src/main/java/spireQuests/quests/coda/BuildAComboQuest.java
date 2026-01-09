package spireQuests.quests.coda;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;

public class BuildAComboQuest extends AbstractQuest {

    public BuildAComboQuest() {
        super(QuestType.SHORT, QuestDifficulty.HARD);
        
        new TriggerTracker<AbstractCard>(QuestTriggers.PLAY_CARD, 1){
                @Override
                public String progressString(){
                    return "";
                }
            }.triggerCondition((card) -> card.cost == 1)
            .setResetTrigger(QuestTriggers.TURN_START, t -> true, false)
            .setResetTrigger(QuestTriggers.COMBAT_END, t -> true, false)
            .add(this);
        new TriggerTracker<AbstractCard>(QuestTriggers.PLAY_CARD, 1){
                @Override
                public String progressString(){
                    return "";
                }
            }.triggerCondition((card) -> card.cost == 2)
            .setResetTrigger(QuestTriggers.TURN_START, t -> true, false)
            .setResetTrigger(QuestTriggers.COMBAT_END, t -> true, false)
            .add(this);
        new TriggerTracker<AbstractCard>(QuestTriggers.PLAY_CARD, 1){
                @Override
                public String progressString(){
                    return "";
                }
            }.triggerCondition((card) -> card.cost == 3)
            .setResetTrigger(QuestTriggers.TURN_START, t -> true, false)
            .setResetTrigger(QuestTriggers.COMBAT_END, t -> true, false)
            .add(this);
        addGenericReward();
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
