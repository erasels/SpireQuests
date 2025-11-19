package spireQuests.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import spireQuests.vfx.ShowCardToDrawPileEffect;


public class ShowTempCardInDrawPileAction
        extends AbstractGameAction {
    private final AbstractCard cardToMake;
    private final boolean autoPosition;
    private final float x;
    private final float y;

    public ShowTempCardInDrawPileAction(AbstractCard card, boolean autoPosition, float cardX, float cardY) {
        UnlockTracker.markCardAsSeen(card.cardID);
        setValues(this.target, this.source, 1);
        this.actionType = AbstractGameAction.ActionType.CARD_MANIPULATION;
        this.startDuration = Settings.FAST_MODE ? Settings.ACTION_DUR_FAST : 0.5F;
        this.duration = this.startDuration;
        this.cardToMake = card;
        this.autoPosition = autoPosition;
        this.x = cardX;
        this.y = cardY;
    }

    public ShowTempCardInDrawPileAction(AbstractCard card, boolean autoPosition) {
        this(card, autoPosition, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F);
    }


    public void update() {
        if (this.duration == this.startDuration) {
            AbstractCard c = this.cardToMake.makeStatEquivalentCopy();
            AbstractDungeon.effectList.add(new ShowCardToDrawPileEffect(c, this.x, this.y, this.autoPosition));
            this.duration -= Gdx.graphics.getDeltaTime();
        }
        tickDuration();
    }
}

