package spireQuests.quests.modargo.monsters.realitytwist;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.powers.FlightPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.Anniv8Mod;
import spireQuests.quests.modargo.RealityTwistQuest;

public class BigByrd extends Byrd {
    public BigByrd(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[11];
        this.maxHealth *= 2;
        this.currentHealth *= 2;
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
        this.addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, 1)));
        this.addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                FlightPower flightPower = (FlightPower)BigByrd.this.getPower(FlightPower.POWER_ID);
                if (flightPower != null) {
                    flightPower.amount += 1;
                    ReflectionHacks.setPrivate(flightPower, FlightPower.class, "storedAmount", flightPower.amount);
                    flightPower.updateDescription();
                }
                this.isDone = true;
            }
        });
    }

    @Override
    protected void loadAnimation(String atlasUrl, String skeletonUrl, float scale) {
        super.loadAnimation(atlasUrl, skeletonUrl, scale / 1.25f);
    }
}
