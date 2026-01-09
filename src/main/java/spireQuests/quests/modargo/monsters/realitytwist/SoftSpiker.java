package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.beyond.Spiker;
import com.megacrit.cardcrawl.powers.ThornsPower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class SoftSpiker extends Spiker {
    public SoftSpiker(float x, float y) {
        super(x, y);
        this.name = RealityTwistQuest.enemyNames[9];
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ReducePowerAction(this, this, ThornsPower.POWER_ID, AbstractDungeon.ascensionLevel >= 17 ? 4 : 2));
    }
}
