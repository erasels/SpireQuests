package spireQuests.quests.ramchops.cards;

import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import spireQuests.abstracts.AbstractSQCard;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.adp;
import static spireQuests.util.Wiz.atb;

public class SelloutAdvertisementCard extends AbstractSQCard {
    public final static String ID = makeID("SelloutAdCard");
    private Random rng;

    public SelloutAdvertisementCard() {
        super(ID, "ramchops",1, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        rng = new Random((long) this.uuid.hashCode());
        ExhaustiveVariable.setBaseValue(this, 3);
        this.shuffleBackIntoDrawPile = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new VFXAction(new RainingGoldEffect(10, true)));
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }


    @Override
    public void upp() {

    }

    public void setupRandomAd(){
        this.misc = this.rng.random(1, this.cardStrings.EXTENDED_DESCRIPTION.length);

        this.rawDescription += " NL ";
        this.rawDescription += this.cardStrings.EXTENDED_DESCRIPTION[this.misc - 1];
        this.initializeDescription();
    }

    @SpirePatch2(clz = CardGroup.class, method = "initializeDeck")
    public static class ResetDescriptionForCombatPatch {
        @SpirePostfixPatch
        public static void resetDescriptionForCombat(CardGroup __instance) {
            for (AbstractCard c : __instance.group) {
                if (c instanceof SelloutAdvertisementCard) {
                    ((SelloutAdvertisementCard) c).setupRandomAd();;
                }
            }
        }
    }
}

