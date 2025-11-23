package spireQuests.quests.gk;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.BurningBlood;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import spireQuests.Anniv8Mod;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.gk.cards.StoneArmor;
import spireQuests.quests.gk.cards.Taunt;
import spireQuests.quests.gk.cards.Unrelenting;
import spireQuests.quests.gk.monsters.ICEliteMonster;

import java.util.ArrayList;

import static spireQuests.Anniv8Mod.makeID;

public class BountyICQuest extends AbstractQuest {
    private static final String ID = makeID(BountyICQuest.class.getSimpleName());

    public BountyICQuest() {
        super(QuestType.SHORT, QuestDifficulty.NORMAL);

        new TriggerTracker<>(QuestTriggers.VICTORY, 1)
                .triggerCondition((x) -> AbstractDungeon.getCurrRoom().eliteTrigger &&
                        ICEliteMonster.ID.equals(AbstractDungeon.lastCombatMetricKey))
                .setFailureTrigger(QuestTriggers.ACT_CHANGE)
                .add(this);
    }

    @Override
    public float getTitleScale() {
        return 0.8f;
    }

    @Override
    public String getRewardsText() {
        return localization.TEXT[3];
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.actNum == 1;
    }

    @SpirePatch2(clz = CombatRewardScreen.class, method = "setupItemReward")
    public static class RewardReplacementPatch {
        @SpirePostfixPatch
        public static void patch(CombatRewardScreen __instance) {
            boolean replacedCards = false, replacedRelic = false;
            BountyICQuest q = (BountyICQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && quest.isCompleted())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                for (RewardItem reward : __instance.rewards) {
                    if (!replacedCards && reward.type == RewardItem.RewardType.CARD) {
                        PredefinedCardReward.skip_rolling_cards = true;
                        RewardItem newReward = new RewardItem();
                        PredefinedCardReward.skip_rolling_cards = false;
                        __instance.rewards.set(__instance.rewards.indexOf(reward), newReward);
                        replacedCards = true;
                    } else if (!replacedRelic && reward.type == RewardItem.RewardType.RELIC) {
                        RewardItem newReward = new RewardItem(new BurningBlood());
                        __instance.rewards.set(__instance.rewards.indexOf(reward), newReward);
                        replacedRelic = true;
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "loading_post_combat");
                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[3]};
            }
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
    @SpirePatch2(clz = AbstractDungeon.class, method = "getColorlessRewardCards")
    public static class PredefinedCardReward {
        public static boolean skip_rolling_cards = false;

        @SpireInsertPatch(locator = PostNumCardsLocator.class, localvars = {"retVal", "numCards"})
        public static void patch(ArrayList<AbstractCard> retVal, @ByRef int[] numCards) {
            if(skip_rolling_cards) {
                numCards[0] = -1;
                retVal.clear();
                retVal.add(new Unrelenting());
                retVal.add(new Taunt());
                retVal.add(new StoneArmor());
            }
        }

        private static class PostNumCardsLocator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ModHelper.class, "isModEnabled");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    // Remove quest once the room is left, needs this annoying time since card rewards are re-generated and I don't want to introduce a custom eward type
    @SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
    public static class AutoCompleteQuestLater {
        @SpireInsertPatch(locator = Locator.class)
        public static void enteringRoomPatch(AbstractDungeon __instance, SaveFile file) {
            if (AbstractDungeon.currMapNode != null) {
                BountyICQuest q = (BountyICQuest) QuestManager.quests().stream()
                        .filter(quest -> ID.equals(quest.id) && quest.isCompleted())
                        .findAny()
                        .orElse(null);
                if(q != null) {
                    QuestManager.completeQuest(q);
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractDungeon.class, method = "getEliteMonsterForRoomCreation")
    public static class SpawnElite {
        @SpirePrefixPatch
        public static SpireReturn<MonsterGroup> replacementPatch() {
            // if this quest exists
            BountyICQuest q = (BountyICQuest) QuestManager.quests().stream()
                    .filter(quest -> ID.equals(quest.id) && !quest.isCompleted() && !quest.isFailed())
                    .findAny()
                    .orElse(null);
            if(q != null) {
                Anniv8Mod.logger.info("Replacing ELITE with Ironclad");
                AbstractDungeon.lastCombatMetricKey = ICEliteMonster.ID;
                return SpireReturn.Return(new MonsterGroup(new ICEliteMonster()));
            }
            return SpireReturn.Continue();
        }
    }
}
