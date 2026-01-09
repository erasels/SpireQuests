package spireQuests.quests.coda.relics;

import static spireQuests.Anniv8Mod.makeID;

import spireQuests.abstracts.AbstractSQRelic;

public class KeyringRelic extends AbstractSQRelic {

    public static final String ID = makeID(KeyringRelic.class.getSimpleName());

    public KeyringRelic() {
        super(ID, "coda", RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public void onChestOpen(boolean bossChest) {
        if (bossChest && !this.usedUp) {
            this.flash();
            this.usedUp();
        }
    }
    
}
