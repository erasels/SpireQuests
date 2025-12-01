package spireQuests.quests.modargo.cards.bloodfire;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.VerticalAuraEffect;

import static spireQuests.Anniv8Mod.makeID;

public class PowerFromTheDark extends BloodfireRitualCard {
    public static final String ID = makeID(PowerFromTheDark.class.getSimpleName());

    public PowerFromTheDark() {
        super(ID);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(p, new VerticalAuraEffect(Color.BLACK, p.hb.cX, p.hb.cY), 0.33F));
        this.addToBot(new MakeTempCardInDrawPileAction(new VoidCard(), 1, false, true, true));
    }
}
