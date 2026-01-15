package spireQuests.quests.luaviper.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import spireQuests.quests.luaviper.patches.CardCreationDetection;
import spireQuests.util.Wiz;

public class MarkCardsAsRapidImprovisationAction extends AbstractGameAction {
    private boolean flag;

    public MarkCardsAsRapidImprovisationAction(boolean flag) {
        this.flag = flag;
    }

    public void update() {
        CardCreationDetection.RapidImprovisationCardDetection.active.set(Wiz.p(), flag);
        isDone = true;
    }
}
