package spireQuests.quests.modargo.relics;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class VolatileStardust extends AbstractSQRelic {
    public static final String ID = makeID(VolatileStardust.class.getSimpleName());

    public VolatileStardust() {
        super(ID, "modargo", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    @Override
    public int onAttacked(DamageInfo info, int damageAmount) {
        if (damageAmount == 0 && info.type != DamageInfo.DamageType.THORNS && info.type != DamageInfo.DamageType.HP_LOSS && info.owner != null && info.owner != AbstractDungeon.player) {
            this.flash();
            this.addToTop(new DamageAction(info.owner, new DamageInfo(AbstractDungeon.player, 7, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE, true));
        }
        return damageAmount;
    }
}
