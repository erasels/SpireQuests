package spireQuests.quests.jackrenoson.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.FireBreathingPower;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;

public class Whelp extends AbstractSQRelic {
    public static final String ID = makeID(Whelp.class.getSimpleName());

    public Whelp() {
        super(ID, "jackrenoson", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atPreBattle() {
        addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new FireBreathingPower(AbstractDungeon.player, 1 + AbstractDungeon.player.gold/100)));
    }

    @Override
    public void onGainGold() {
        if(Wiz.isInCombat()){
            this.counter = 1 + AbstractDungeon.player.gold/100;
        }
    }

    @Override
    public void onLoseGold() {
        if(Wiz.isInCombat()){
            this.counter = 1 + AbstractDungeon.player.gold/100;
        }
    }

    @Override
    public void onVictory() {
        this.counter = 1 + AbstractDungeon.player.gold/100;
    }


}
