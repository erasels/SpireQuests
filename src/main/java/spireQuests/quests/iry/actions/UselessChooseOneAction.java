package spireQuests.quests.iry.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

// A choose one screen that literally does nothing
// Done for humor purposes, but can be removed if deemed too annoying
// used by ClumsyNightmare
public class UselessChooseOneAction extends AbstractGameAction {
    private AbstractPlayer p;

    public UselessChooseOneAction() {
        this.p = AbstractDungeon.player;
        this.amount = 1;
        this.duration = this.startDuration = Settings.ACTION_DUR_FAST;
    }

    // basic structure taken from NightmareAction, but does nothing except put card back in hand instead
    public void update() {
         if (this.duration == Settings.ACTION_DUR_FAST) {
            if (this.p.hand.isEmpty()) {
                this.isDone = true;
            } else if (this.p.hand.size() == 1) {
                this.isDone = true;
            } else {
                AbstractDungeon.handCardSelectScreen.open("???", 1, false, false);
                this.tickDuration();
            }
        } else {
            if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
                AbstractCard tmpCard = AbstractDungeon.handCardSelectScreen.selectedCards.getBottomCard();
                AbstractDungeon.player.hand.addToHand(tmpCard);
                AbstractDungeon.handCardSelectScreen.selectedCards.clear();
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            }
            this.tickDuration();
        }
    }
}
