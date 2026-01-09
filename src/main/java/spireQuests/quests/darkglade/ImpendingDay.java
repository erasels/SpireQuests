package spireQuests.quests.darkglade;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;

public class ImpendingDay extends AbstractSQCard {
    public static final String ID = makeID(ImpendingDay.class.getSimpleName());
    private static final int DAMAGE = 8;
    private static final int UP_DAMAGE = 2;
    private static final int HEAL = 2;
    private static final int UP_HEAL = 1;

    public ImpendingDay() {
        super(ID, "darkglade", 1, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY);
        baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = HEAL;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractDungeon.actionManager.addToBottom(new ImpendingDayAction(m, new DamageInfo(p, this.damage, this.damageTypeForTurn), magicNumber, this));
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
        upgradeMagicNumber(UP_HEAL);
    }
}
