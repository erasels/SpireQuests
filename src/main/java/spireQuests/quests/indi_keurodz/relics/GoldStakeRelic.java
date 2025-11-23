package spireQuests.quests.indi_keurodz.relics;

import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQRelic;

public class GoldStakeRelic extends AbstractSQRelic {
    public static final String ID = Anniv8Mod.makeID(GoldStakeRelic.class.getSimpleName());

    public GoldStakeRelic() {
        super(ID, "indi_keurodz", RelicTier.SPECIAL, LandingSound.FLAT);
    }

}
