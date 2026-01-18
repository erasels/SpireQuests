package spireQuests.util;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.SharpHidePower;

import static com.megacrit.cardcrawl.core.Settings.GameLanguage.KOR;

public class LanguageUtils {
    public static String formatLanguage(String rawText, String coloredText) {
        if (Settings.language == KOR) {
            String fix = "를";
            char last = coloredText.charAt(coloredText.length() - 1);
            if (last >= 0xAC00 && last <= 0xD7A3) {
                fix = ((last - 0xAC00) % 28 == 0) ? "를" : "을";
            }
            return String.format(rawText, coloredText, fix);
        }
        return String.format(rawText, coloredText);
    }

    @SpirePatch(clz = SharpHidePower.class, method = "updateDescription")
    public static class BlessedSharpHidePatch {
        @SpirePostfixPatch
        public static void patch(SharpHidePower __instance) {
            if (Settings.language != KOR) return;
            __instance.description = "공격 카드를 사용할 때마다 피해를 #b" + __instance.amount + " 받습니다.";
        }
    }
}
