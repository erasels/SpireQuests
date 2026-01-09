package spireQuests.quests.gk.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;

@NoPools
@NoCompendium
public class StoneArmor extends AbstractSQCard {
    public static final String ID = makeID(StoneArmor.class.getSimpleName());

    public StoneArmor() {
        super(ID, "gk", 1, CardType.POWER, CardRarity.SPECIAL, CardTarget.SELF, CardColor.RED);

        baseMagicNumber = magicNumber = 5;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        Wiz.applyToSelf(new PlatedArmorPower(p, magicNumber));
        Wiz.applyToSelf(new DexterityPower(p, -1));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(2);
    }
}