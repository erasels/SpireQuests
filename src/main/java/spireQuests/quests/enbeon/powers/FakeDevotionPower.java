package spireQuests.quests.enbeon.powers;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.watcher.ChangeStanceAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.watcher.MantraPower;
import com.megacrit.cardcrawl.stances.DivinityStance;
import spireQuests.abstracts.AbstractSQPower;

import static spireQuests.Anniv8Mod.makeID;

public class FakeDevotionPower extends AbstractSQPower {
    public static String POWER_ID = makeID(FakeDevotionPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FakeDevotionPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, "enbeon", AbstractPower.PowerType.BUFF, false, owner, amount);
        updateDescription();
        this.loadRegion("devotion");
    }

    public void atEndOfRound() {
        if (owner instanceof AbstractMonster) {
            flash();
            addToBot(new ApplyPowerAction(owner, owner, new FakeMantraPower(owner, amount), amount));
        }
    }

    @Override
    public void atStartOfTurnPostDraw() {
        if (owner instanceof AbstractPlayer) {
            // Behaviour copied from basegame DevotionPower
            flash();
            if (!owner.hasPower(MantraPower.POWER_ID) && this.amount >= 10) {
                addToBot(new ChangeStanceAction(DivinityStance.STANCE_ID));
            } else {
                addToBot(new ApplyPowerAction(this.owner, this.owner, new MantraPower(this.owner, this.amount)));
            }
        }
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }
}
