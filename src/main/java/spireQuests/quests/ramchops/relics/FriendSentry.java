package spireQuests.quests.ramchops.relics;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDiscardAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.tempCards.Insight;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;
import spireQuests.abstracts.AbstractSQRelic;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.util.Wiz.*;


public class FriendSentry extends AbstractSQRelic {

    public static String ID = makeID(FriendSentry.class.getSimpleName());

    public FriendSentry() {
        super(ID, "ramchops", RelicTier.SPECIAL, LandingSound.HEAVY);
    }

    @Override
    public void atBattleStart() {
        counter = 0;
    }

    @Override
    public void atTurnStart() {
        counter += 1;

        if (counter % 2 == 1){
            this.flash();
            AbstractDungeon.actionManager.addToBottom(new SFXAction("THUNDERCLAP"));
            AbstractDungeon.actionManager.addToBottom(new VFXAction(adp(), new ShockWaveEffect(adp().hb.cX, adp().hb.cY, Color.ROYAL, ShockWaveEffect.ShockWaveType.ADDITIVE), 0.5F));
            AbstractDungeon.actionManager.addToBottom(new FastShakeAction(AbstractDungeon.player, 0.6F, 0.2F));
            AbstractDungeon.actionManager.addToBottom(new MakeTempCardInDiscardAction(new Insight(), 1));
        }
    }

    @Override
    public void onVictory() {
        counter = -1;
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(DESCRIPTIONS[0]);
    }
}
