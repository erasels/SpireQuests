package spireQuests.quests.iry.cards;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.NoPools;
import basemod.patches.com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen.NoCompendium;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.purple.Blasphemy;
import com.megacrit.cardcrawl.cards.red.Offering;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.watcher.EndTurnDeathPower;
import com.megacrit.cardcrawl.vfx.combat.OfferingEffect;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.util.CardArtRoller;

import static spireQuests.Anniv8Mod.makeID;

// ClumsyBlasphemy:
// like base game blasphemy but with ethereal and no upsides
// obtained from LessonOfTheAscetic quest
@NoPools
@NoCompendium
public class ClumsyBlasphemy extends AbstractSQCard {
    public final static String ID = makeID("ClumsyBlasphemy");

    public ClumsyBlasphemy() {
        super(ID, "iry",1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF, CardColor.PURPLE);
        this.isEthereal = true;
        this.exhaust = true;

        // appropriating the art roller to reuse base game art, hope this is alright!
        CardArtRoller.infos.put(ID, new CardArtRoller.ReskinInfo(Blasphemy.ID, 0.5f, 0.25f, 0.5f, 1f, false));
        setDisplayRarity(CardRarity.RARE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new ApplyPowerAction(p, p, new EndTurnDeathPower(p)));
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upp() {

    }
}
