package spireQuests.util;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;

import static spireQuests.quests.gk.util.HermitCompatUtil.initGunEffect;

public class CompatUtil {
    public static final String CARDISTRY_ID = "anniv5:Cardistry";
    public static final String SNAPSHOT_ID = "hermit:Snapshot";

    public static AbstractCard.CardColor PM_COLOR = AbstractCard.CardColor.COLORLESS;
    public static AbstractCard.CardColor HERMIT_COLOR = AbstractCard.CardColor.COLORLESS;

    public static AbstractCard.CardTags DEADON_TAG = AbstractCard.CardTags.EMPTY;

    public static void postInit() {
        AbstractCard card = CardLibrary.getCard(CARDISTRY_ID);
        if (card != null) {
            PM_COLOR = card.color;
        }

        card = CardLibrary.getCard(SNAPSHOT_ID);
        if (card != null) {
            HERMIT_COLOR = card.color;
            DEADON_TAG = card.tags.get(0);
            initGunEffect();
        }
    }

    public static boolean pmLoaded() {
        return Loader.isModLoaded("anniv5");
    }
}
