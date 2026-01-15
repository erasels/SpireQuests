package spireQuests.quests.luaviper.patches;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Chrysalis;
import com.megacrit.cardcrawl.cards.colorless.Metamorphosis;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDrawPileEffect;
import spireQuests.cardmods.QuestboundMod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.luaviper.RapidImprovisationQuest;
import spireQuests.quests.luaviper.actions.MarkCardsAsRapidImprovisationAction;
import spireQuests.util.Wiz;

public class CardCreationDetection {
    @SpirePatch2(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
    public static class RapidImprovisationCardDetection {
        public static SpireField<Boolean> active = new SpireField<>(() -> false);
    }

    @SpirePatch2(clz = Metamorphosis.class, method = "use")
    public static class MetamorphosisPatch {
        @SpirePrefixPatch
        public static void prefix(AbstractCard __instance) {
            if (CardModifierManager.hasModifier(__instance, QuestboundMod.ID))
                Wiz.atb(new MarkCardsAsRapidImprovisationAction(true));
        }

        @SpirePostfixPatch
        public static void postfix(AbstractCard __instance) {
            if (CardModifierManager.hasModifier(__instance, QuestboundMod.ID))
                Wiz.atb(new MarkCardsAsRapidImprovisationAction(false));
        }
    }

    @SpirePatch2(clz = Chrysalis.class, method = "use")
    public static class ChrysalisPatch {
        @SpirePrefixPatch
        public static void prefix(AbstractCard __instance) {
            if (CardModifierManager.hasModifier(__instance, QuestboundMod.ID))
                Wiz.atb(new MarkCardsAsRapidImprovisationAction(true));
        }

        @SpirePostfixPatch
        public static void postfix(AbstractCard __instance) {
            if (CardModifierManager.hasModifier(__instance, QuestboundMod.ID))
                Wiz.atb(new MarkCardsAsRapidImprovisationAction(false));
        }
    }

    @SpirePatch2(clz = ShowCardAndAddToDrawPileEffect.class, method = SpirePatch.CONSTRUCTOR,
            paramtypez = {AbstractCard.class, float.class, float.class, boolean.class, boolean.class, boolean.class})
    public static class CreateCardPatch {
        @SpirePostfixPatch
        public static void postfix(AbstractCard ___card) {
            if (CardCreationDetection.RapidImprovisationCardDetection.active.get(Wiz.adp())) {
                AbstractQuest quest = null;
                for (AbstractQuest q : QuestManager.quests()) {
                    if (q instanceof RapidImprovisationQuest) {
                        quest = q;
                    }
                }
                if (quest != null) {
                    ((RapidImprovisationQuest) quest).nextAddedCard = ___card;
                    AbstractQuest.Tracker tracker = new AbstractQuest.TriggerTracker<>(QuestTriggers.PLAY_CARD, 1)
                            .triggerCondition((card) -> card == ___card)
                            .setFailureTrigger(QuestTriggers.COMBAT_END)
                            .add(quest);
                }
            }
        }
    }
}
