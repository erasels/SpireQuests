package spireQuests.quests.gk.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.actions.defect.AnimateOrbAction;
import com.megacrit.cardcrawl.actions.defect.EvokeOrbAction;
import com.megacrit.cardcrawl.actions.defect.EvokeWithoutRemovingOrbAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static spireQuests.Anniv8Mod.makeID;

@NoPools
@NoCompendium
public class TripleCast extends AbstractBPCard {
    public static final String ID = makeID(TripleCast.class.getSimpleName());

    public TripleCast() {
        super(ID, "gk", 1, CardType.SKILL, CardRarity.BASIC, CardTarget.NONE, CardColor.BLUE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AnimateOrbAction(1));
        addToBot(new EvokeWithoutRemovingOrbAction(1));
        addToBot(new AnimateOrbAction(1));
        addToBot(new EvokeWithoutRemovingOrbAction(1));
        addToBot(new AnimateOrbAction(1));
        addToBot(new EvokeOrbAction(1));
    }

    @Override
    public void upp() {
        upgradeBaseCost(0);
    }
}
