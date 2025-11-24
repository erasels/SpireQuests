package spireQuests.quests.soytheproton.relics;

import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;

public class MagicBoot extends AbstractSQRelic {
    public static final String ID = makeID(MagicBoot.class.getSimpleName());
    public MagicBoot() {
        super(ID, "soytheproton", RelicTier.SPECIAL, LandingSound.MAGICAL);
    }

    public int onAttackToChangeDamage(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && damageAmount < 7) {

            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            return 7;
        }
        return damageAmount;
    }

}
