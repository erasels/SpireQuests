package spireQuests.quests.ramchops.relics;

import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.adp;


public class SmoresRelic extends AbstractSQRelic {

    private final int MAX_HP_GAIN = 10;


    public static String ID = makeID(SmoresRelic.class.getSimpleName());

    public SmoresRelic() {
        super(ID, "ramchops", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        adp().increaseMaxHp(MAX_HP_GAIN, true);
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(DESCRIPTIONS[0], MAX_HP_GAIN);
    }
}
