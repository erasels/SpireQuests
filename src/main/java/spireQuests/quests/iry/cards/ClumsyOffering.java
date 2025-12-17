package spireQuests.quests.iry.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.purple.Blasphemy;
import com.megacrit.cardcrawl.cards.red.Offering;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.util.CardArtRoller;

import static spireQuests.Anniv8Mod.makeID;

// ClumsyOffering:
// like base game offering but with ethereal and no upsides
// obtained from LessonOfTheBlood quest
@NoPools
@NoCompendium
public class ClumsyOffering extends AbstractSQCard {
    public final static String ID = makeID("ClumsyOffering");

    public ClumsyOffering() {
        super(ID, "iry",0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, CardColor.RED);
        this.isEthereal = true;
        this.exhaust = true;

        // appropriating the art roller to reuse base game art, hope this is alright!
        CardArtRoller.infos.put(ID, new CardArtRoller.ReskinInfo(Offering.ID, 0.5f, 0.25f, 0.5f, 1f, false));
        setDisplayRarity(CardRarity.RARE);
    }

    // direct copy from Base Game Offering
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (Settings.FAST_MODE) {
            this.addToBot(new VFXAction(new OfferingEffect(), 0.1F));
        } else {
            this.addToBot(new VFXAction(new OfferingEffect(), 0.5F));
        }
        this.addToBot(new LoseHPAction(p, p, 6));
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upp() {

    }
}
