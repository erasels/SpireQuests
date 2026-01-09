package spireQuests.quests.modargo.cards.embracethecurse;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;

import static spireQuests.Anniv8Mod.makeID;

public class SkullGaze extends EmbraceTheCurseCard {
    public static final String ID = makeID(SkullGaze.class.getSimpleName());

    public SkullGaze() {
        super(ID, 1, CardType.SKILL, CardTarget.ENEMY);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(m, p, new StrengthPower(m, -this.countCurses() / 2)));
    }

    private int countCurses() {
        return (int)AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.color == CardColor.CURSE).count();
    }

    @Override
    public void upp() {
        this.upgradeBaseCost(0);
    }
}
