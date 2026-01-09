package spireQuests.quests.coda.patches;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.AbstractRelic.RelicTier;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;

import javassist.CtBehavior;
import spireQuests.quests.coda.relics.KeyringRelic;

public class ExtraBossRelicPatch {
    // Add additional relic option
    @SpirePatch(
        clz = BossChest.class,
        method = SpirePatch.CONSTRUCTOR
    )
    public static class BossChestPatch {
        @SpirePostfixPatch
        private static void Postfix(BossChest __instance) {
            for (AbstractRelic r: AbstractDungeon.player.relics.stream().filter((r) -> r.relicId.equals(KeyringRelic.ID) && !r.usedUp).collect(Collectors.toList())) {
                __instance.relics.add(AbstractDungeon.returnRandomRelic(RelicTier.BOSS));
            }
        }
    }

    // Postion nth Relic in circle on screen
    @SpirePatch(
        clz = BossRelicSelectScreen.class,
        method = "open"
    )
    public static class RelicScreenPatch {

        @SpireInsertPatch(
            locator = RelicPlacementLocator.class
        )
        private static void Insert(BossRelicSelectScreen __instance, ArrayList<AbstractRelic> chosenRelics) {

            if (chosenRelics.size() > 3) {
                List<AbstractRelic> relicsToAdd = chosenRelics.subList(3, chosenRelics.size());
                for (AbstractRelic r: relicsToAdd) {
                    r.spawn(0.0F, 0.0F);
                    __instance.relics.add(r);
                }
            }
        
            float cX = Settings.WIDTH / 2.0F;
            float cY = AbstractDungeon.floorY + 294.0F * Settings.scale;
        
            if (__instance.relics.size() > 3) {
                for (int i = 0; i < __instance.relics.size(); i++) {
                    float angle = 45.0F + (i * 360.0F / __instance.relics.size());
                    float radius = 128.0F;

                    float xOffset = radius * MathUtils.cosDeg(angle);
                    float yOffset = radius * MathUtils.sinDeg(angle);

                    AbstractRelic r = __instance.relics.get(i);
                    r.currentX = cX + xOffset;
                    r.currentY = cY + yOffset;
                    r.hb.move(r.currentX, r.currentY);
                }
            }
        }
    
        private static class RelicPlacementLocator extends SpireInsertLocator {
    
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher matcher = new Matcher.FieldAccessMatcher(BossRelicSelectScreen.class, "relics");
                int[] found = LineFinder.findAllInOrder(ctMethodToPatch, matcher);
                return new int[]{found[found.length-1]};
            }
    
        }
    }
    
}
