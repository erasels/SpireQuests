package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.monsters.exordium.GremlinWizard;
import com.megacrit.cardcrawl.powers.BarricadePower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class ArmoredGremlinWizard extends GremlinWizard {
    public ArmoredGremlinWizard(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[1];
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ApplyPowerAction(this, this, new BarricadePower(this)));
        this.addToBot(new GainBlockAction(this, 25));
    }
}
