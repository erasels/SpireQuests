package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.monsters.exordium.SlaverRed;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class PoisonedSlaverRed extends SlaverRed {
    public PoisonedSlaverRed(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[7];
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ApplyPowerAction(this, this, new PoisonPower(this, this, 8)));
        this.addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, -5)));
    }
}
