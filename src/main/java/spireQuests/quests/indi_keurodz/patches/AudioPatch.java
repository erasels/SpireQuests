package spireQuests.quests.indi_keurodz.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.RentalStickerModifier;

public class AudioPatch {
    @SpirePatch2(clz = BaseMod.class, method = "publishAddAudio")
    public static class AddAudio {

        public static void Prefix() {
            BaseMod.addAudio(RentalStickerModifier.MODIFIER_ID,
                    Anniv8Mod.modID + "Resources/images/indi_keurodz/coin1.ogg");
        }
    }
}
