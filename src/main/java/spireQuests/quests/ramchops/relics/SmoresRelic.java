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
import static spireQuests.util.Wiz.adp;


public class SmoresRelic extends AbstractSQRelic {

    private final int MAX_HP_GAIN = 10;


    public static String ID = makeID(SmoresRelic.class.getSimpleName());

    public SmoresRelic() {
        super(ID, "ramchops", RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        adp().increaseMaxHp(MAX_HP_GAIN, true);
    }

    @Override
    public String getUpdatedDescription() {
        return String.format(DESCRIPTIONS[0], MAX_HP_GAIN);
    }
}
