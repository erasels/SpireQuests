package spireQuests.quests.modargo.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.OnReceivePowerRelic;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.*;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class SpireAlloyHelmet extends AbstractSQRelic implements OnReceivePowerRelic {
    public static final String ID = makeID(SpireAlloyHelmet.class.getSimpleName());

    public SpireAlloyHelmet() {
        super(ID, "modargo", RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public boolean onReceivePower(AbstractPower power, AbstractCreature creature) {
        if (AbstractDungeon.player.hasPower(ArtifactPower.POWER_ID)) {
            return true;
        }
        boolean matchingPower = power instanceof WeakPower || power instanceof FrailPower;
        if (matchingPower && power.amount > 0) {
            this.flash();
            power.amount--;
            return power.amount > 0;
        }
        return true;
    }
}
