package spireQuests.quests.enbeon.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import spireQuests.abstracts.AbstractSQPower;

import static spireQuests.Anniv8Mod.makeID;

public class FakeMantraPower extends AbstractSQPower {
    public static String POWER_ID = makeID(FakeMantraPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FakeMantraPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, "enbeon", PowerType.BUFF, false, owner, amount);
        updateDescription();
        this.loadRegion("mantra");
    }

    public void playApplyPowerSfx() {
        CardCrawlGame.sound.play("POWER_MANTRA", 0.05F);
    }

    private void divinityCheck() {
        if (amount >= 10) {
            addToTop(new ReducePowerAction(owner, owner, this, 10));
            addToTop(new ApplyPowerAction(owner, owner, new InvisibleDivinityForMonsterPower(owner)));
        }
    }

    @Override
    public void onInitialApplication() {
        super.onInitialApplication();
        divinityCheck();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        divinityCheck();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }
}
