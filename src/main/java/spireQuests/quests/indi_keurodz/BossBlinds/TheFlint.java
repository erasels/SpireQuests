
package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.powers.FlintPower;
import spireQuests.util.Wiz;

@SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfTurnRelics")
public class TheFlint {
    @SpirePrefixPatch()
    public static void ApplyFlintPowerOnTurnStart() {
        if (ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Flint.frames)) {
            Wiz.applyToSelf(new FlintPower(AbstractDungeon.player));
        }
    }
}
