package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.patches.ShowMarkedNodesOnMapPatch.ImageField;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.BalatroQuest.BossBlind;
import spireQuests.quests.indi_keurodz.modifiers.RentalModifier;

public class TheTooth {
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class ChargeCardMoneyPatch {
        @SpirePostfixPatch
        public static void LoseGoldPerCard() {
            if (!ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.ID, BossBlind.Tooth.frames))
                return;

            AbstractDungeon.player.loseGold(1);
            CardCrawlGame.sound.play(RentalModifier.ID);

        }
    }
}
