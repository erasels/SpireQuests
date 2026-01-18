package spireQuests.quests.ramchops.cards;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.variables.ExhaustiveVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.BufferPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.vfx.RainingGoldEffect;
import com.megacrit.cardcrawl.vfx.combat.MiracleEffect;
import spireQuests.abstracts.AbstractSQCard;
import spireQuests.util.Wiz;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.applyToSelf;
import static spireQuests.util.Wiz.atb;

public class BlessingHeaven extends AbstractSQCard {
    public final static String ID = makeID("BlessingHeaven");
    private Random rng;

    public BlessingHeaven() {
        super(ID, "ramchops",0, CardType.SKILL, CardRarity.SPECIAL, CardTarget.SELF);
        this.exhaust = true;
        this.selfRetain = true;
        this.baseMagicNumber = this.magicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        atb(new VFXAction(new MiracleEffect(Color.GOLD, Color.GOLD, "HEAL_1")));
        applyToSelf(new BufferPower(p, magicNumber));
    }

    @Override
    public void upp() {
        upgradeMagicNumber(1);
    }
}

