package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.actions.common.ShuffleAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.util.Wiz;

@SpirePatch2(clz = AbstractRoom.class, method = "endTurn")
public class TheWater {
    @SpirePostfixPatch
    public static void ShuffleDiscardIntoDraw() {
        if (ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID,
                BalatroQuest.BossBlind.Water.frames)) {
            Wiz.atb(new EmptyDeckShuffleAction());
            Wiz.atb(new ShuffleAction(AbstractDungeon.player.drawPile, false));
        }
    }
}
