package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.beyond.GiantHead;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class TinyHead extends GiantHead {
    public TinyHead() {
        super();
        this.name = RealityTwistQuest.enemyNames[0];
        this.currentHealth /= 3;
        this.hb_w /= 3;
        this.hb_h /= 3;
        this.hb_y += 30.0F * Settings.scale;
        this.hb = new Hitbox(this.hb_w, this.hb_h);
        this.healthHb = new Hitbox(this.hb_w, 72.0F * Settings.scale);
        this.refreshHitboxLocation();
        this.refreshIntentHbLocation();
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, -20)));
    }

    @Override
    protected void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        super.loadAnimation(atlasUrl, skeletonUrl, scale * 3);
    }
}
