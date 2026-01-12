package spireQuests.quests.iry.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;


// TwosCompanyPatches:
// Tracks enemies alive at start of turn
public class TwosCompanyPatches {
    public static int enemiesAliveThisTurn = 0;

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
    public static class OnTurnStart {
        @SpirePrefixPatch
        public static void turnStartPatch() {
            enemiesAliveThisTurn = 0;
            for(AbstractMonster m2: AbstractDungeon.getCurrRoom().monsters.monsters){
                if (!m2.isDeadOrEscaped()) {
                    enemiesAliveThisTurn++;
                }
            }
        }
    }
}
