package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.monsters.beyond.OrbWalker;
import com.megacrit.cardcrawl.powers.StrengthPower;
import spireQuests.quests.modargo.RealityTwistQuest;
import spireQuests.quests.modargo.powers.IgnitePower;

public class BurningOrbWalker extends OrbWalker {
    public BurningOrbWalker(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.fightNames[3];
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, -10)));
        this.addToBot(new ApplyPowerAction(this, this, IgnitePower.create(this, 15)));
    }
}
