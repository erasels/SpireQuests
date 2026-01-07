package spireQuests.quests.indi_keurodz.BossBlinds;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.quests.indi_keurodz.modifiers.RentalStickerModifier;
import spireQuests.quests.indi_keurodz.patches.ShowBossBlindsOnMapPatch;

public class TheTooth {
    @SpirePatch(clz = AbstractPlayer.class, method = "useCard")
    public static class ChargeCardMoneyPatch {
        @SpirePostfixPatch
        public static void LoseGoldPerCard() {
            BalatroQuest.BossBlind blind = ShowBossBlindsOnMapPatch.BossBlindField.blind.get(AbstractDungeon.getCurrMapNode());
            if (blind == null)
                return;
            switch (blind) {
                case Tooth:
                    AbstractDungeon.player.loseGold(1);
                    CardCrawlGame.sound.play(RentalStickerModifier.MODIFIER_ID);
                    break;
            }

        }
    }
}
