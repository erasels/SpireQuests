package spireQuests.quests.modargo.relics;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class FloralGarland extends AbstractSQRelic {
    public static final String ID = makeID(FloralGarland.class.getSimpleName());

    public FloralGarland() {
        super(ID, "modargo", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void onCardDraw(AbstractCard drawnCard) {
        if (!this.grayscale && drawnCard.type == AbstractCard.CardType.CURSE) {
            this.flash();
            this.addToBot(new DrawCardAction(1));
            this.grayscale = true;
        }
    }

    @Override
    public void atBattleStart() {
        this.grayscale = false;
    }

    @Override
    public void onVictory() {
        this.grayscale = false;
    }
}
