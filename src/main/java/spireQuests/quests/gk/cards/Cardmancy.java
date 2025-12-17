package spireQuests.quests.gk.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ReboundPower;
import spireQuests.quests.gk.powers.UpgradeNextPower;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.CompatUtil.PM_COLOR;
import static spireQuests.util.CompatUtil.pmLoaded;

@NoPools
@NoCompendium
public class Cardmancy extends AbstractBPCard {
    public static final String ID = makeID(Cardmancy.class.getSimpleName());

    public Cardmancy() {
        super(ID, "gk", 0, CardType.SKILL, CardRarity.BASIC, CardTarget.SELF, PM_COLOR);

        if (pmLoaded()) {
            setBackgroundTexture(
                    "anniv5Resources/images/512/coreset/skill.png",
                    "anniv5Resources/images/1024/coreset/skill.png");
        }

        baseBlock = block = 2;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        blck();
        Wiz.applyToSelf(new ReboundPower(p));
        Wiz.applyToSelf(new UpgradeNextPower(p, 1));
    }

    @Override
    public void upp() {
        upgradeBlock(2);
    }
}
