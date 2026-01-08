package spireQuests.quests.dayvig.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.JAX;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class MutagenBlood extends AbstractSQRelic {
    private static final int HP_LOSS = 6;

    public static final String ID = makeID(MutagenBlood.class.getSimpleName());

    public boolean jaxPlayed = false;

    public MutagenBlood() {
        super(ID, "dayvig", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onUseCard(AbstractCard targetCard, UseCardAction useCardAction) {
        if (targetCard.cardID.equals(JAX.ID)){
            jaxPlayed = true;
            this.grayscale = false;
        }
    }

    @Override
    public void onVictory(){
        if (!jaxPlayed){
            this.flash();
            this.addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            this.addToTop(new LoseHPAction(AbstractDungeon.player, AbstractDungeon.player, HP_LOSS, AbstractGameAction.AttackEffect.POISON));
        }
    }

    @Override
    public void atPreBattle(){
        jaxPlayed = false;
        this.grayscale = true;
    }
}

