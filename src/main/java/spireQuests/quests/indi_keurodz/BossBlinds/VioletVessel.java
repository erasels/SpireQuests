package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;
import com.megacrit.cardcrawl.vfx.combat.DamageNumberEffect;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;

import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.patches.ShowBossBlindsOnMapPatch.BossBlindField;


public class VioletVessel {

    private static boolean hasBuffedEnemies = false;

    @SpirePatch2(clz = MonsterRoom.class, method = "onPlayerEntry")
    public static class MonsterRoomResetPatch {
        @SpirePostfixPatch
        public static void Postfix(MonsterRoom __instance) {
            hasBuffedEnemies = false;
        }
    }

    @SpirePatch2(clz = MonsterRoomElite.class, method = "onPlayerEntry")
    public static class EliteRoomResetPatch {
        @SpirePostfixPatch
        public static void Postfix(MonsterRoomElite __instance) {
            hasBuffedEnemies = false;
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class EnemyHealthBuffPatch {
        @SpirePostfixPatch
        public static void Postfix(AbstractRoom __instance) {
            if (hasBuffedEnemies || AbstractDungeon.getCurrRoom().monsters == null) return;

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
                case Needle:
                    hpMod = -0.5f;
                    break;
                default:
                    hasBuffedEnemies = true;
                    return;
            }

            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (m != null && !m.isDying && !m.isEscaping) {
                    int extraHP = (int)(m.maxHealth * hpMod);

                    if (hpMod >= 0) {
                        AbstractDungeon.effectList.add(new HealEffect(
                                m.hb.cX,
                                m.hb.cY,
                                extraHP
                        ));
                        m.maxHealth += extraHP;
                    } else {
                        AbstractDungeon.effectList.add(new DamageNumberEffect(
                                m,
                                m.hb.cX,
                                m.hb.cY,
                                -extraHP
                        ));
                    }

                    m.currentHealth += extraHP;
                    m.healthBarUpdatedEvent();
                }
            }
            hasBuffedEnemies = true;
        }
    }
}