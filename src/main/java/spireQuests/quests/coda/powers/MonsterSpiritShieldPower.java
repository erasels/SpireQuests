package spireQuests.quests.coda.powers;

import static spireQuests.Anniv8Mod.makeID;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.NextTurnBlockPower;

import basemod.interfaces.CloneablePowerInterface;
import spireQuests.abstracts.AbstractSQPower;

public class MonsterSpiritShieldPower extends AbstractSQPower implements CloneablePowerInterface{

    private static final String ID = makeID(MonsterSpiritShieldPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(ID);
    private static final String NAME = powerStrings.NAME;
    private static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public MonsterSpiritShieldPower(AbstractCreature owner, int amount) {
        super(ID, NAME, "coda", PowerType.BUFF, true, owner, amount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        flash();
        addToBot(new ApplyPowerAction(owner, owner, new NextTurnBlockPower(owner, this.amount)));
        // addToBot(new GainBlockAction(owner, this.amount));
    }
    
    
    @Override
    public void atStartOfTurn() {
        flash();
        addToBot(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public AbstractPower makeCopy() {
        return new MonsterSpiritShieldPower(owner, amount);
    }
    
}
