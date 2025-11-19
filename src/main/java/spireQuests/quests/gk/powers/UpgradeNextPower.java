package spireQuests.quests.gk.powers;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import spireQuests.abstracts.AbstractSQPower;

import static spireQuests.Anniv8Mod.makeID;

public class UpgradeNextPower extends AbstractSQPower {
    public static String POWER_ID = makeID(UpgradeNextPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private boolean justEvoked = true;

    public UpgradeNextPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, "gk", PowerType.BUFF, true, owner, amount);
    }

    @Override
    public void updateDescription() {
        if (this.amount > 1) {
            this.description = String.format(DESCRIPTIONS[1], amount);
        } else {
            this.description = DESCRIPTIONS[0];
        }
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        if (this.justEvoked) {
            this.justEvoked = false;
            return;
        }

        if (card.type != AbstractCard.CardType.POWER && card.canUpgrade()) {
            flash();
            card.upgrade();
        }

        addToBot(new ReducePowerAction(owner, owner, this, 1));
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }

}
