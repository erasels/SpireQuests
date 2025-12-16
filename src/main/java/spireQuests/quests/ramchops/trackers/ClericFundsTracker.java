package spireQuests.quests.ramchops.trackers;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import spireQuests.quests.OptionalTriggerTracker;
import spireQuests.quests.Trigger;

public class ClericFundsTracker extends OptionalTriggerTracker<Integer> {

    public static final Trigger<Integer> CLERIC_FUND_CHANGE = new Trigger<>();
    public static final Trigger<Integer> SKIP_GOLD = new Trigger<>();

    public ClericFundsTracker(){
        super(SKIP_GOLD, 0, true);
    }

    @Override
    public void trigger(Integer param) {
        localCount += param;
        CLERIC_FUND_CHANGE.trigger(localCount);
    }

    @SpirePatch2(
            clz = CombatRewardScreen.class,
            method = "clear")
    public static class SkipGoldPatch{
        @SpirePrefixPatch
        public static void SkipGold(CombatRewardScreen __instance){

            for(RewardItem r : __instance.rewards){
                if(r.type == RewardItem.RewardType.GOLD){
                    SKIP_GOLD.trigger(r.goldAmt);

                    if(r.bonusGold != 0){
                        SKIP_GOLD.trigger(r.goldAmt);
                    }
                }
            }
        }
    }
}
