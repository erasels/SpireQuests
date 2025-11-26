package spireQuests.quests.gk.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.abstracts.AbstractSQPower;

import static spireQuests.Anniv8Mod.makeID;

public class FakeDemonFormPower extends AbstractSQPower implements CloneablePowerInterface {
    public static String POWER_ID = makeID(FakeDemonFormPower.class.getSimpleName());
    public static final String NAME = CardCrawlGame.languagePack.getPowerStrings("Demon Form").NAME;
    public static final String[] DESCRIPTIONS = CardCrawlGame.languagePack.getPowerStrings("Ritual").DESCRIPTIONS;

    public FakeDemonFormPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, "gk", PowerType.BUFF, false, owner, amount);
        updateDescription();
        this.loadRegion("demonForm");
    }

    @Override
    public void updateDescription() {
        if(owner instanceof AbstractMonster) {
            description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
        } else {
            PowerStrings dms = CardCrawlGame.languagePack.getPowerStrings("Demon Form");
            description = dms.DESCRIPTIONS[0] + amount + dms.DESCRIPTIONS[1];
        }
    }

    public void atEndOfRound() {
        if (owner instanceof AbstractMonster) {
            flash();
            addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount), amount));
        }

    }

    @Override
    public void atStartOfTurnPostDraw() {
        if (owner instanceof AbstractPlayer) {
            flash();
            addToBot(new ApplyPowerAction(owner, owner, new StrengthPower(owner, amount), amount));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new FakeDemonFormPower(owner, amount);
    }
}
