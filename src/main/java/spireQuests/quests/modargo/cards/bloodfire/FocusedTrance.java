package spireQuests.quests.modargo.cards.bloodfire;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.FrailPower;
import com.megacrit.cardcrawl.vfx.combat.FastingEffect;

import static spireQuests.Anniv8Mod.makeID;

public class FocusedTrance extends BloodfireRitualCard {
    public static final String ID = makeID(FocusedTrance.class.getSimpleName());
    private static final int FRAIL = 2;

    public FocusedTrance() {
        super(ID);
        this.magicNumber = this.baseMagicNumber = FRAIL;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new VFXAction(new FastingEffect(p.hb.cX, p.hb.cY, Color.BLUE), 0.33F));
        this.addToBot(new ApplyPowerAction(p, p, new FrailPower(p, this.magicNumber, false)));
    }
}
