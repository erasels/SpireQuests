package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.exordium.Sentry;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class SuperSentry extends Sentry {
    public SuperSentry(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[3];
        this.hb_w *= 1.25f;
        this.hb_h *= 1.25f;
        this.hb = new Hitbox(this.hb_w, this.hb_h);
        this.healthHb = new Hitbox(this.hb_w, 72.0F * Settings.scale);
        this.refreshHitboxLocation();
        this.refreshIntentHbLocation();
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 10)));
        this.addToBot(new ApplyPowerAction(this, this, new MetallicizePower(this, 20)));
        this.addToBot(new GainBlockAction(this, 20));
        this.addToBot(new ApplyPowerAction(this, this, new ArtifactPower(this, 1)));
    }

    @Override
    protected void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        super.loadAnimation(atlasUrl, skeletonUrl, scale / 1.25f);
    }
}
