package spireQuests.quests.snumodder.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.quests.snumodder.ZilliaxDeluxe3000Quest;

import static spireQuests.Anniv8Mod.makeID;

public class HaywireModule extends AbstractModuleCard {
    public static final String ID = makeID(HaywireModule.class.getSimpleName());

    public HaywireModule() {
        super(ID, "snumodder", 0, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.COLORLESS);
        baseDamage = damage = 8;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
        moduleUse(p, m, null);
    }

    @Override
    public void upp() {
        upgradeDamage(2);
    }

    @Override
    public void moduleUse(AbstractPlayer p, AbstractMonster m, AbstractCard card) {
        addToBot(new LoseHPAction(p, p, 2));
    }

    @Override
    public ZilliaxDeluxe3000Quest.ZilliaxModules getModule() {
        return ZilliaxDeluxe3000Quest.ZilliaxModules.HAYWIRE;
    }
}
