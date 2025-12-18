package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;
import spireQuests.quests.indi_keurodz.patches.ShowBossBlindsOnMapPatch.BossBlindField;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;

public class VioletVessel {

    private static boolean hasBuffedEnemies = false;

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class EnemyHealthBuffPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractRoom __instance) {
            if (hasBuffedEnemies) return;
            BossBlind blind = BossBlindField.blind.get(AbstractDungeon.getCurrMapNode());
            if (blind == null) {
                hasBuffedEnemies = true;
                return;
            }

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

            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (m != null && !m.isDying && !m.isEscaping) {
                    int extraHP = (int)(m.maxHealth * hpMod);
                    m.maxHealth += extraHP;
                    m.currentHealth += extraHP;
                    m.healthBarUpdatedEvent();

                    AbstractDungeon.effectList.add(new HealEffect(
                            m.hb.cX,
                            m.hb.cY,
                            extraHP
                    ));
                }
            }
            hasBuffedEnemies = true;
        }
    }

    @SpirePatch2(clz = MonsterRoom.class, method = "onPlayerEntry")
    public static class ResetFlagPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            hasBuffedEnemies = false;
        }
    }

    @SpirePatch2(clz = MonsterRoomElite.class, method = "onPlayerEntry")
    public static class ResetEliteFlagPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            hasBuffedEnemies = false;
        }
    }
}