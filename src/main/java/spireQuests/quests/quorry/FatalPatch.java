package spireQuests.quests.quorry;

import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.powers.MinionPower;
import spireQuests.patches.QuestTriggers;

@SpirePatch2(clz = DamageAction.class, method = "update")
public class FatalPatch {

    @SpireInsertPatch(
            loc=93
    )
    public static void Update(DamageAction __instance, DamageInfo ___info)
    {
        if (((__instance.target).isDying || __instance.target.currentHealth <= 0) && !__instance.target.halfDead &&
                !__instance.target.hasPower(MinionPower.POWER_ID)) {
            Object fatalSource = DamageModifierManager.BoundDamageInfoFields.instigatingObject.get(___info);
            if (fatalSource instanceof AbstractCard)
                QuestTriggers.FATAL_CARD.trigger((AbstractCard) fatalSource);
        }
    }
}