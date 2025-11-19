package spireQuests.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.PotionHelper;
import com.megacrit.cardcrawl.potions.AbstractPotion;

import basemod.patches.com.megacrit.cardcrawl.helpers.PotionLibrary.PotionHelperGetPotions;

@SpirePatch2(clz = PotionHelperGetPotions.class, method = "Postfix")
public class PotionPoolPatch {
    @SpirePostfixPatch
    public static void addPackPotions(ArrayList<String> __result, AbstractPlayer.PlayerClass c, boolean getAll) {
        if (getAll)
            return;

        __result.removeIf((id)->{
            AbstractPotion p = PotionHelper.getPotion(id);
            return (p == null || p.rarity == AbstractPotion.PotionRarity.PLACEHOLDER);
        });
    }
}
