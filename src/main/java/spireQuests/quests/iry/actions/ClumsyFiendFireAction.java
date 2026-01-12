package spireQuests.quests.iry.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

// ClumsyFiendFireAction:
// Used by clumsy fiend fire, code structure fully copied from FiendFireAction for consistency
public class ClumsyFiendFireAction extends AbstractGameAction {
    private float startingDuration;

    public ClumsyFiendFireAction() {
        this.actionType = ActionType.WAIT;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.duration = this.startingDuration;
    }

    public void update() {
        int count = AbstractDungeon.player.hand.size();

        for(int i = 0; i < count; ++i) {
            if (Settings.FAST_MODE) {
                this.addToTop(new ExhaustAction(1, true, true, false, Settings.ACTION_DUR_XFAST));
            } else {
                this.addToTop(new ExhaustAction(1, true, true));
            }
        }

        this.isDone = true;
    }
}
