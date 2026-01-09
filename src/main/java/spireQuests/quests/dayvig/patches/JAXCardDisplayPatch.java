package spireQuests.quests.dayvig.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.JAX;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.quests.dayvig.relics.MutagenBlood;

import java.util.ArrayList;

public class JAXCardDisplayPatch {
    @SpirePatch(clz = AbstractCard.class, method = "renderDescription", paramtypez = SpriteBatch.class)

    public static class JAXModifyDescription {

        public static ArrayList<AbstractCard> cardsModified = new ArrayList<>();

        public static void Prefix(AbstractCard __instance, SpriteBatch sb) {
            if (__instance.cardID.equals(JAX.ID) && AbstractDungeon.player.hasRelic(MutagenBlood.ID) && !cardsModified.contains(__instance)) {
                String unmodifiedDescription = __instance.rawDescription;
                int firstThreeIndex = __instance.rawDescription.indexOf('3');
                __instance.rawDescription = unmodifiedDescription.substring(0, firstThreeIndex) + '1' + unmodifiedDescription.substring(firstThreeIndex+1);
                __instance.initializeDescription();
                cardsModified.add(__instance);
            }
        }
    }
}
