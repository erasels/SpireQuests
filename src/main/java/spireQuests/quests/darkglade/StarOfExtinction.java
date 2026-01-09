package spireQuests.quests.darkglade;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.atb;

public class StarOfExtinction extends AbstractSQCard {
    public static final String ID = makeID(StarOfExtinction.class.getSimpleName());
    private static final int DAMAGE = 30;
    private static final int UP_DAMAGE = 15;

    public StarOfExtinction() {
        super(ID, "darkglade", 3, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ALL_ENEMY);
        baseDamage = DAMAGE;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new StarOfExtinctionAction(1, false));
        allDmg(AbstractGameAction.AttackEffect.FIRE);
    }

    @Override
    public void upp() {
        upgradeDamage(UP_DAMAGE);
    }
}
