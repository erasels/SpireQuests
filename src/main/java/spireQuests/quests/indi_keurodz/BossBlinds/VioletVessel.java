package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;
import spireQuests.quests.indi_keurodz.patches.ShowBossBlindsOnMapPatch.BossBlindField;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class VioletVessel {

    @SpirePatch2(clz = AbstractMonster.class, method = "init")
    public static class EnemyHealthBuffPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractMonster __instance) {
            BossBlind blind = BossBlindField.blind.get(AbstractDungeon.getCurrMapNode());
            if (blind == null) return;

            float hpMod = 0.0f;
            switch (blind) {
                case VioletVessel:
                    hpMod = 0.2f;
                    break;
                case Wall:
                    hpMod = 0.1f;
                    break;
                default:
                    return;
            }

            int extraHP = (int)(__instance.maxHealth * hpMod);
            __instance.maxHealth += extraHP;
            __instance.currentHealth += extraHP;
            __instance.healthBarUpdatedEvent();

            AbstractDungeon.effectList.add(new HealEffect(
                    __instance.hb.cX,
                    __instance.hb.cY,
                    extraHP
            ));
        }
    }
}
