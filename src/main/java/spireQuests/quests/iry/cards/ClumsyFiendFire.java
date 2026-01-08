package spireQuests.quests.iry.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.cards.red.FiendFire;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.quests.iry.actions.ClumsyFiendFireAction;
import spireQuests.util.CardArtRoller;

import static spireQuests.Anniv8Mod.makeID;

// ClumsyFiendFire:
// like base game fiend fire but with ethereal and no upsides
// obtained from LessonOfTheFlame quest
@NoPools
@NoCompendium
public class ClumsyFiendFire extends AbstractSQCard {
    public final static String ID = makeID("ClumsyFiendFire");

    public ClumsyFiendFire() {
        super(ID, "iry",2, CardType.ATTACK, CardRarity.SPECIAL, CardTarget.SELF, CardColor.RED);
        this.isEthereal = true;
        this.exhaust = true;

        // appropriating the art roller to reuse base game art, hope this is alright!
        CardArtRoller.infos.put(ID, new CardArtRoller.ReskinInfo(FiendFire.ID, 0.5f, 0.25f, 0.5f, 1f, false));
        setDisplayRarity(CardRarity.RARE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ClumsyFiendFireAction());
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upp() {

    }
}
