package spireQuests.quests.gk.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.FreeAttackPower;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;

@NoPools
@NoCompendium
public class Unrelenting extends AbstractSQCard {
    public static final String ID = makeID(Unrelenting.class.getSimpleName());

    public Unrelenting() {
        super(ID, "gk", 2, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.ENEMY, CardColor.RED);

        baseDamage = damage = 12;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        dmg(m, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        Wiz.applyToSelf(new FreeAttackPower(p, 1));
    }

    @Override
    public void upp() {
        upgradeDamage(4);
    }
}
