package spireQuests.patches;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.relics.AbstractRelic;

@SpirePatch2(cls = "pickyrelics.patches.RelicLinkPatch", method = "getRelicWithFallback", paramtypez = { AbstractRelic.RelicTier.class }, requiredModId = "pickyrelics")
public class FixPickyRelicsForStarterRelics {
    @SpirePrefixPatch
    public static void fixPickyRelics(@ByRef AbstractRelic.RelicTier[] tier) {
        // This is a bug in Picky Relics that we're fixing through a patch. (Granted, it's a bug that only comes up when
        // another mod puts a starter relic in the relic rewards, but still.)
        // It's not valid to try to generate random starter relics, but Picky Relics tries to do it anyway when it sees
        // Burning Blood in the rewards from the Bounty Ironclad quest and crashes. Instead, have starter relics treated
        // as common relics.
        if (tier[0] == AbstractRelic.RelicTier.STARTER) {
            tier[0] = AbstractRelic.RelicTier.COMMON;
        }
    }
}
