package spireQuests.quests.indi_keurodz.relics;

import com.megacrit.cardcrawl.helpers.PowerTip;

import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQRelic;

public class GoldStakeRelic extends AbstractSQRelic {
    public static final String ID = Anniv8Mod.makeID(GoldStakeRelic.class.getSimpleName());

    public GoldStakeRelic() {
        super(ID, "indi_keurodz", RelicTier.SPECIAL, LandingSound.FLAT);
        if (descriptionUpgraded()) {
            upgradeDescription();
        }
    }

    public void upgradeDescription() {
        this.description = DESCRIPTIONS[1];
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();

        counter = -2;
        flash();
    }

    public boolean descriptionUpgraded() {
        return counter == -2;
    }

}
