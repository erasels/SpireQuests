package spireQuests.quests.enbeon.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.options.ConfirmPopup;
import spireQuests.quests.enbeon.monsters.WatcherEliteMonster;

// These patches target everywhere in basegame where stance idle SFX are stopped
// In this way, the Watcher elite's SFX should hopefully act the same as regular stance SFX
@SuppressWarnings("unused")
public class DivinityIdleSfxPatches {
    public static void checkCurrentMonsters() {
        MonsterGroup mg = AbstractDungeon.getMonsters();
        if (mg == null) return;
        for (AbstractMonster mo : mg.monsters) {
            if (mo instanceof WatcherEliteMonster) {
                ((WatcherEliteMonster) mo).stopIdleSfx();
            }
        }
    }

    @SpirePatch(clz = AbstractRoom.class, method = "endBattle")
    @SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
    public static class AbstractRoomAndDungeonPatch {
        @SpirePostfixPatch
        public static void stopSfxOnBattleEnd() {
            checkCurrentMonsters();
        }
    }

    @SpirePatch(clz = ConfirmPopup.class, method = "yesButtonEffect")
    public static class ConfirmPopupPatch {
        @SpireInsertPatch(loc = 239)
        public static void stopSfxOnConfirmExitPopup() {
            checkCurrentMonsters();
        }
    }

    @SpirePatch(clz = DeathScreen.class, method = SpirePatch.CONSTRUCTOR)
    public static class DeathScreenPatch {
        @SpirePrefixPatch
        public static void stopSfxOnDeath(DeathScreen __instance, MonsterGroup m) {
            if (m == null) return;
            for (AbstractMonster mo : m.monsters) {
                if (mo instanceof WatcherEliteMonster) {
                    ((WatcherEliteMonster) mo).stopIdleSfx();
                }
            }
        }
    }
}
