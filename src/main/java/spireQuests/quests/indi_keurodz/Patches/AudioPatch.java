package spireQuests.quests.indi_keurodz.Patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.audio.SoundMaster;
import spireQuests.Anniv8Mod;
import spireQuests.quests.indi_keurodz.RentalStickerModifier;

public class AudioPatch {
    @SpirePatch2(clz = SoundMaster.class, method = SpirePatch.CONSTRUCTOR)
    public static class AddAudio {
        public static void Postfix() {
            BaseMod.addAudio(RentalStickerModifier.MODIFIER_ID,
                    Anniv8Mod.modID + "Resources/images/indi_keurodz/coin1.ogg");
        }
    }
}
