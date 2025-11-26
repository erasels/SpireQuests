package spireQuests.quests.modargo.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import spireQuests.abstracts.AbstractSQPower;

import static spireQuests.Anniv8Mod.makeID;

public class FakeFocusPower extends AbstractSQPower {
    public static String POWER_ID = makeID(FakeFocusPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FakeFocusPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, "modargo", PowerType.BUFF, false, owner, amount);
        updateDescription();
        this.loadRegion("focus");
        this.canGoNegative = true;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
