package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.monsters.beyond.Exploder;
import com.megacrit.cardcrawl.powers.ExplosivePower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class DudExploder extends Exploder {
    public DudExploder(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[8];
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new RemoveSpecificPowerAction(this, this, ExplosivePower.POWER_ID));
    }

    @Override
    protected void getMove(int num) {
        this.setMove((byte)1, Intent.ATTACK, this.damage.get(0).base);
    }
}
