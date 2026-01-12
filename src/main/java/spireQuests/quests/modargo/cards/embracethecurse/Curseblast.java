package spireQuests.quests.modargo.cards.embracethecurse;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static spireQuests.Anniv8Mod.makeID;

public class Curseblast extends EmbraceTheCurseCard {
    public static final String ID = makeID(Curseblast.class.getSimpleName());
    private static final int DAMAGE = 10;
    private static final int DAMAGE_PER_CURSE = 2;
    private static final int UPGRADE_DAMAGE_PER_CURSE = 1;

    public Curseblast() {
        super(ID, 2, CardType.ATTACK, CardTarget.ALL_ENEMY);
        this.baseDamage = DAMAGE;
        this.magicNumber = this.baseMagicNumber = DAMAGE_PER_CURSE;
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new DamageAllEnemiesAction(p, this.multiDamage, this.damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE));
    }

    @Override
    public void applyPowers() {
        int curses = this.countCurses();
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * curses;
        super.applyPowers();
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != realBaseDamage;
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int curses = this.countCurses();
        int realBaseDamage = this.baseDamage;
        this.baseDamage += this.magicNumber * curses;
        super.calculateCardDamage(m);
        this.baseDamage = realBaseDamage;
        this.isDamageModified = this.damage != realBaseDamage;
    }

    private int countCurses() {
        return (int)AbstractDungeon.player.masterDeck.group.stream().filter(c -> c.color == CardColor.CURSE).count();
    }

    @Override
    public void upp() {
        this.upgradeMagicNumber(UPGRADE_DAMAGE_PER_CURSE);
    }
}
