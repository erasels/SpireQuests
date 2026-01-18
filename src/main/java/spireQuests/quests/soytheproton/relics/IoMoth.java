package spireQuests.quests.soytheproton.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.BetterOnLoseHpRelic;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import spireQuests.abstracts.AbstractSQRelic;
import spireQuests.quests.soytheproton.patches.MothFriendPatch;
import spireQuests.quests.soytheproton.vfx.ExclamationParticle;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.quests.soytheproton.MothQuest.MOTH_SFX;

public class IoMoth extends AbstractSQRelic implements BetterOnLoseHpRelic {
    public static final String ID = makeID(IoMoth.class.getSimpleName());
    public IoMoth() {
        super(ID, "soytheproton", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    public static boolean hasTakenDamageThisCombat;

    public void atPreBattle() {
        hasTakenDamageThisCombat = false;
    }

    public void justEnteredRoom(AbstractRoom room) {
        grayscale = true;
    }


    public void atTurnStart() {
        if(hasTakenDamageThisCombat) flash();
    }

    @Override
    public int betterOnLoseHp(DamageInfo info, int damageAmount) {
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount >= 1 && !hasTakenDamageThisCombat) {
            flash();
            CardCrawlGame.sound.play(MOTH_SFX,0.1F);
            AbstractDungeon.effectList.add(new ExclamationParticle(MothFriendPatch.drawX,MothFriendPatch.drawY + 50.0F * Settings.scale));
            AbstractDungeon.player.gameHandSize++;
            addToTop(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            hasTakenDamageThisCombat = true;
            grayscale = false;
        }
        return damageAmount;
    }
}
