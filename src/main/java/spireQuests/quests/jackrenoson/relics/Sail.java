package spireQuests.quests.jackrenoson.relics;

import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.quests.jackrenoson.cards.SetSail;

import java.util.Objects;

import static spireQuests.Anniv8Mod.makeID;

public class Sail extends AbstractSQRelic {
    public static final String ID = makeID(Sail.class.getSimpleName());

    public Sail() {
        super(ID, "jackrenoson", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void atBattleStart() {
        addToBot(new MakeTempCardInHandAction(new SetSail()));
        this.counter = 0;
    }

    @Override
    public void onPlayerEndTurn() {
        if (!this.grayscale) {
            ++this.counter;
        }

        if (this.counter == 3) {
            this.flash();
            for(AbstractCard card : AbstractDungeon.player.hand.group){
                if(Objects.equals(card.cardID, SetSail.ID)){
                    this.addToBot(new ExhaustSpecificCardAction(card, AbstractDungeon.player.hand));
                }
            }
            for(AbstractCard card : AbstractDungeon.player.discardPile.group){
                if(Objects.equals(card.cardID, SetSail.ID)){
                    this.addToBot(new ExhaustSpecificCardAction(card, AbstractDungeon.player.discardPile));
                }
            }
            for(AbstractCard card : AbstractDungeon.player.drawPile.group){
                if(Objects.equals(card.cardID, SetSail.ID)){
                    this.addToBot(new ExhaustSpecificCardAction(card, AbstractDungeon.player.drawPile));
                }
            }
            this.counter = -1;
            this.grayscale = true;
        }
    }

    @Override
    public void onVictory() {
        this.counter = -1;
        this.grayscale = false;
    }
}
