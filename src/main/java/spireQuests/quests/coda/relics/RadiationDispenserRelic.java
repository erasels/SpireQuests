package spireQuests.quests.coda.relics;

import static spireQuests.Anniv8Mod.makeID;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;

import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.quests.coda.potions.NuclearJuicePotion;

public class RadiationDispenserRelic extends AbstractSQRelic {

    public static float chanceBuff = 0.032F;
    public static float chance = chanceBuff;
    private static final String RELIC_ID = makeID(RadiationDispenserRelic.class.getSimpleName());

    public RadiationDispenserRelic() {
        super(RELIC_ID, "coda", RelicTier.SPECIAL, LandingSound.SOLID);
    }

    @Override
    public void onVictory() {
        super.onVictory();
        if (AbstractDungeon.potionRng.random() < chance) {
            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(new NuclearJuicePotion()));
            chance = chanceBuff;
            return;
        }
        chance += chanceBuff;
    }
    
}
