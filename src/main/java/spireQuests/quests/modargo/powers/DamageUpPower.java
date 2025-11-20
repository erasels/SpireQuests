package spireQuests.quests.modargo.powers;

import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import spireQuests.Anniv8Mod;
import spireQuests.abstracts.AbstractSQPower;

public class DamageUpPower extends AbstractSQPower {
    public static final String POWER_ID = Anniv8Mod.makeID(DamageUpPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DamageUpPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, "modargo", NeutralPowertypePatch.NEUTRAL, false, owner, amount);
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        return damage + (damage * (this.amount / 100f));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }
}

