package spireQuests.quests.modargo.cards.bloodfire;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

import static spireQuests.Anniv8Mod.makeID;

public class EssenceOfFlame extends BloodfireRitualCard {
    public static final String ID = makeID(EssenceOfFlame.class.getSimpleName());

    public EssenceOfFlame() {
        super(ID);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        this.addToBot(new VFXAction(p, new FlashAtkImgEffect(p.hb.cX, p.hb.cY, AbstractGameAction.AttackEffect.FIRE), 0.33F));
        this.addToBot(new MakeTempCardInHandAction(new Burn(), 1));
    }
}
