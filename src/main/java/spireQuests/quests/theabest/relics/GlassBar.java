package spireQuests.quests.theabest.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;

public class GlassBar extends AbstractSQRelic {
    public static final String ID = makeID(GlassBar.class.getSimpleName());

    private static final int BASE_BLOCK = 12;
    private static final int BLOCK_LOSS_PER_CARD = 2;

    public GlassBar() {
        super(ID, "theabest", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onPlayerEndTurn() {
        if (counter > 0) {
            addToBot(new RelicAboveCreatureAction(Wiz.p(), this));
            addToBot(new GainBlockAction(AbstractDungeon.player, counter));
        }
    }

    private void updateCounter() {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int cardsPlayed = AbstractDungeon.actionManager.cardsPlayedThisTurn.size();
                counter = Math.max(0, BASE_BLOCK - BLOCK_LOSS_PER_CARD * cardsPlayed);
                this.isDone = true;
            }
        });
    }

    @Override
    public void atTurnStart() {
        updateCounter();
    }

    @Override
    public void onPlayCard(AbstractCard c, AbstractMonster m) {
        updateCounter();
    }

    @Override
    public void atPreBattle() {
        counter = BASE_BLOCK;
    }

    // Hide counter outside of combat
    @Override
    public void onVictory() {
        counter = -1;
    }
}
