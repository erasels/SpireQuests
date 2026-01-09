package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.beyond.Maw;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class MiniMaw extends Maw {
    public MiniMaw(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[6];
        this.currentHealth /= 2;
        this.hb_w /= 2;
        this.hb_h /= 2;
        this.hb = new Hitbox(this.hb_w, this.hb_h);
        this.healthHb = new Hitbox(this.hb_w, 72.0F * Settings.scale);
        this.refreshHitboxLocation();
        this.refreshIntentHbLocation();
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, -5)));
    }

    @Override
    protected void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        super.loadAnimation(atlasUrl, skeletonUrl, scale * 2);
    }
}
