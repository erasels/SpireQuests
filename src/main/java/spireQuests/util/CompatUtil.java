package spireQuests.util;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import spireQuests.Anniv8Mod;

public class CompatUtil {
    public static final String CARDISTRY_ID = "anniv5:Cardistry";
    public static final String SNAPSHOT_ID = "hermit:Snapshot";

    public static AbstractCard.CardColor PM_COLOR = AbstractCard.CardColor.COLORLESS;
    public static AbstractCard.CardColor HERMIT_COLOR = AbstractCard.CardColor.COLORLESS;

    public static AbstractCard.CardTags DEADON_TAG = AbstractCard.CardTags.EMPTY;
    public static AbstractGameAction.AttackEffect HERMIT_GUN_EFFECT = AbstractGameAction.AttackEffect.NONE;

    public static void postInit() {
        AbstractCard card = CardLibrary.getCard(CARDISTRY_ID);
        if(card != null) {
            PM_COLOR = card.color;
        }

        card = CardLibrary.getCard(SNAPSHOT_ID);
        if (card != null) {
            HERMIT_COLOR = card.color;
            DEADON_TAG = card.tags.get(0);
            initGunEffect();
        }
    }

    public static void initGunEffect() {
        try {
            Class<?> enumPatchClass = Class.forName("hermit.patches.EnumPatch");
            HERMIT_GUN_EFFECT = (AbstractGameAction.AttackEffect) enumPatchClass.getField("HERMIT_GUN").get(null);
        } catch(ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            Anniv8Mod.logger.warn("Couldn't get HERMIT_GUN attack effect");
        }
    }
}
