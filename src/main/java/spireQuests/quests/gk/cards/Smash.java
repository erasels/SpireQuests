package spireQuests.quests.gk.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.evacipated.cardcrawl.mod.stslib.actions.common.AllEnemyApplyPowerAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import static spireQuests.Anniv8Mod.makeID;

@NoPools
@NoCompendium
public class Smash extends AbstractBPCard {
    public static final String ID = makeID(Smash.class.getSimpleName());

    public Smash() {
        super(ID, "gk", 2, CardType.ATTACK, CardRarity.BASIC, CardTarget.ALL_ENEMY, CardColor.RED);
        isMultiDamage = true;
        exhaust = true;

        baseDamage = damage = 8;
        baseMagicNumber = magicNumber = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        allDmg(AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        addToBot(new AllEnemyApplyPowerAction(p, magicNumber, mon -> new VulnerablePower(mon, magicNumber, false)));
    }


    @Override
    public void upp() {
        upgradeDamage(2);
        upgradeMagicNumber(1);
    }
}
