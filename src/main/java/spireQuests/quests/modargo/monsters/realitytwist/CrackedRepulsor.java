package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.monsters.beyond.Repulsor;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class CrackedRepulsor extends Repulsor {
    public CrackedRepulsor(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[10];
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ApplyPowerAction(this, this, new WeakPower(this, 5, false)));
        this.addToBot(new ApplyPowerAction(this, this, new VulnerablePower(this, 5, false)));
    }
}
