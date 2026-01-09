package spireQuests.quests.gk.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.evacipated.cardcrawl.mod.stslib.actions.common.AllEnemyApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;

@NoPools
@NoCompendium
public class Taunt extends AbstractSQCard {
    public static final String ID = makeID(Taunt.class.getSimpleName());

    public Taunt() {
        super(ID, "gk", 1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.ALL, CardColor.RED);

        baseBlock = block = 7;
        baseMagicNumber = magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        blck();
        addToBot(new AllEnemyApplyPowerAction(p, magicNumber, (mon) -> new VulnerablePower(mon, magicNumber, false)));
    }

    @Override
    public void upp() {
        upgradeBlock(1);
        upgradeMagicNumber(1);
    }
}