package spireQuests.quests.coda.relics;

import static spireQuests.Anniv8Mod.makeID;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.abstracts.AbstractSQRelic;

public class GrapefruitRelic extends AbstractSQRelic{

    private static final String ID = makeID(GrapefruitRelic.class.getSimpleName());
    private static final float PERCENT = 0.2F;

    public GrapefruitRelic() {
        super(ID, "coda", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.increaseMaxHp((int)(AbstractDungeon.player.maxHealth * PERCENT), true);
    }
    
}
