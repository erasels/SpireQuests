package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.city.Mugger;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.RitualPower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class ToughMugger extends Mugger {
    public ToughMugger(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[4];
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
        this.addToBot(new ApplyPowerAction(this, this, new PlatedArmorPower(this, 10)));
        this.addToBot(new ApplyPowerAction(this, this, new RitualPower(this, 2, false)));
    }

    @Override
    protected void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        super.loadAnimation(atlasUrl, skeletonUrl, scale / 1.25f);
    }
}
