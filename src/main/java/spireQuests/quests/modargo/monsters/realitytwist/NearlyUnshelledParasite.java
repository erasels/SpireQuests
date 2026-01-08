package spireQuests.quests.modargo.monsters.realitytwist;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.utility.LoseBlockAction;
import com.megacrit.cardcrawl.monsters.city.ShelledParasite;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import spireQuests.quests.modargo.RealityTwistQuest;

public class NearlyUnshelledParasite extends ShelledParasite {
    public NearlyUnshelledParasite() {
        super();
        this.name = RealityTwistQuest.fightNames[5];
        this.currentHealth -= 13;
    }

    @Override
    public void usePreBattleAction() {
        super.usePreBattleAction();
        this.addToBot(new ReducePowerAction(this, this, PlatedArmorPower.POWER_ID, 13));
        this.addToBot(new LoseBlockAction(this, this, 13));
        this.addToBot(new ApplyPowerAction(this, this, new WeakPower(this, 5, false)));
        this.addToBot(new ApplyPowerAction(this, this, new StrengthPower(this, -1)));
    }
}
