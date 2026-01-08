package spireQuests.quests.iry.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.actions.defect.ShuffleAllAction;
import com.megacrit.cardcrawl.cards.blue.Reboot;
import com.megacrit.cardcrawl.cards.purple.Blasphemy;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.util.CardArtRoller;

import static spireQuests.Anniv8Mod.makeID;

// ClumsyReboot:
// like base game reboot but with ethereal and no upsides
// obtained from LessonOfTheCore quest
@NoPools
@NoCompendium
public class ClumsyReboot extends AbstractSQCard {
    public final static String ID = makeID("ClumsyReboot");

    public ClumsyReboot() {
        super(ID, "iry",0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, CardColor.BLUE);
        this.isEthereal = true;
        this.exhaust = true;

        // appropriating the art roller to reuse base game art, hope this is alright!
        CardArtRoller.infos.put(ID, new CardArtRoller.ReskinInfo(Reboot.ID, 0.5f, 0.25f, 0.5f, 1f, false));
        setDisplayRarity(CardRarity.RARE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ShuffleAllAction());
        this.addToBot(new ShuffleAction(AbstractDungeon.player.drawPile, false));
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upp() {

    }
}
