package spireQuests.quests.enbeon.relics;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class DivineOculus extends AbstractSQRelic {
    private static final int ACTIVATION_TURN = 5;
    private static final int ENERGY_GAIN = 2;

    public static final String ID = makeID(DivineOculus.class.getSimpleName());

    public DivineOculus() {
        super(ID, "enbeon", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public void atBattleStart() {
        counter = 0;
    }

    @Override
    public void atTurnStart() {
        if (!grayscale) counter++;
        if (counter == ACTIVATION_TURN) {
            flash();
            addToBot(new GainEnergyAction(ENERGY_GAIN));
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            counter = -1;
            grayscale = true;
        }
    }

    @Override
    public void onVictory() {
        counter = -1;
        grayscale = false;
    }
}
