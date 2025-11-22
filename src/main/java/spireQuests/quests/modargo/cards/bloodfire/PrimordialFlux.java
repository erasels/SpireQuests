package spireQuests.quests.modargo.cards.bloodfire;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.Slimed;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.MiracleEffect;

import static spireQuests.Anniv8Mod.makeID;

public class PrimordialFlux extends BloodfireRitualCard {
    public static final String ID = makeID(PrimordialFlux.class.getSimpleName());

    public PrimordialFlux() {
        super(ID);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(p, new MiracleEffect(Color.GREEN, Color.LIME, "BLOCK_GAIN_1"), 0.33F));
        this.addToBot(new MakeTempCardInDrawPileAction(new Slimed(), 1, true, true));
    }
}
