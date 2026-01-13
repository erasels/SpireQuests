package spireQuests.quests.indi_keurodz.BossBlinds;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.ShowMarkedNodesOnMapPatch;
import spireQuests.quests.indi_keurodz.BalatroQuest;
import spireQuests.util.Wiz;

public class TheOx {
    @SpirePatch2(clz = AbstractPlayer.class, method = "useCard")
    public static class TrackCardPlays {
        @SpirePostfixPatch
        public static void TrackAndDeductPatch(AbstractCard c) {
            BalatroQuest.trackCardPlay(c.cardID);

            if(ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BalatroQuest.BossBlind.Ox.frames)) {
                if (c.cardID.equals(BalatroQuest.getMostPlayedCard())) {
                    Wiz.p().loseGold(Wiz.p().gold);
                }
            }
        }
    }

    @SpirePatch2(clz = AbstractCard.class, method = "update")
    public static class UpdateOxGlow {
        @SpirePostfixPatch
        public static void UpdateGlow(AbstractCard __instance) {
            if(!ShowMarkedNodesOnMapPatch.ImageField.CheckMarks(AbstractDungeon.currMapNode, BalatroQuest.id, BalatroQuest.BossBlind.Ox.frames)) {
                return;
            }

            if (BalatroQuest.getMostPlayedCard().equals(__instance.cardID)) {
                __instance.glowColor = Color.PINK.cpy();
                __instance.triggerOnGlowCheck();
            }
        }
    }
}
