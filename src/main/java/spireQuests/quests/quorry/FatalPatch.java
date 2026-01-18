package spireQuests.quests.quorry;

import com.evacipated.cardcrawl.mod.stslib.damagemods.DamageModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.MinionPower;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import spireQuests.patches.QuestTriggers;

import java.util.ArrayList;

@SpirePatch2(clz = DamageAction.class, method = "update")
public class FatalPatch {

    @SpireInsertPatch(
            locator=Locator.class
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

    private static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(MonsterGroup.class, "areMonstersBasicallyDead");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
        }
    }
}