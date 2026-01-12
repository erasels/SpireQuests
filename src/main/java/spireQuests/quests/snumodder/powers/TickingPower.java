package spireQuests.quests.snumodder.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import spireQuests.abstracts.AbstractSQMonster;
import spireQuests.abstracts.AbstractSQPower;

import static spireQuests.Anniv8Mod.makeID;

public class TickingPower extends AbstractSQPower {
    public static final String POWER_ID = makeID(TickingPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public static final int COUNT = 3;
    public TickingPower(AbstractCreature owner) {
        super(POWER_ID, NAME, "snumodder", PowerType.BUFF, false, owner, 0);
    }

    @Override
    public void onAfterUseCard(AbstractCard card, UseCardAction action) {
        this.flashWithoutSound();
        ++this.amount;
        if (this.amount == COUNT) {
            this.amount = 0;
            AbstractSQMonster m = ((AbstractSQMonster)owner);
            m.setMoveShortcut((byte)1);
            m.createIntent();
        }
    }

    @Override
    public void atStartOfTurn() {
        this.amount = 0;
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + COUNT + DESCRIPTIONS[1];
    }
}
