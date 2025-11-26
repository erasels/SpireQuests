package spireQuests.quests.modargo;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import spireQuests.patches.QuestTriggers;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.Trigger;
import spireQuests.quests.modargo.relics.CustomerAppreciationAward;

public class PlatinumCafeMemberQuest extends AbstractQuest {
    public static Class<?> anniv7;
    public static Class<?> cafeRoom;
    public static Class<?> abstractCutscene;
    public static Class<?> abstractNPC;
    public static Class<?> abstractBartender;

    public PlatinumCafeMemberQuest() {
        super(QuestType.SHORT, QuestDifficulty.EASY);
        if (Loader.isModLoaded("anniv7")) {
            try {
                anniv7 = Class.forName("spireCafe.Anniv7Mod");
                cafeRoom = Class.forName("spireCafe.CafeRoom");
                abstractCutscene = Class.forName("spireCafe.abstracts.AbstractCutscene");
                abstractNPC = Class.forName("spireCafe.abstracts.AbstractNPC");
                abstractBartender = Class.forName("spireCafe.abstracts.AbstractBartender");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Error retrieving classes from Spire Cafe", e);
            }
        }

        new TriggerTracker<>(CAFE_ENTRY, 1).setFailureTrigger(QuestTriggers.ACT_CHANGE).add(this);
        Tracker bartender = new TriggerTracker<>(CAFE_DONE_WITH_NPC, 1).triggerCondition(b -> b).setFailureTrigger(QuestTriggers.ACT_CHANGE).add(this);
        Tracker patron = new TriggerTracker<>(CAFE_DONE_WITH_NPC, 3).triggerCondition(b -> !b).setFailureTrigger(QuestTriggers.ACT_CHANGE).add(this);
        new TriggerEvent<>(CAFE_ENTRY, cafe -> {
            bartender.show();
            patron.show();
        }).add(this);
        bartender.hide();
        patron.hide();

        addReward(new QuestReward.RelicReward(new CustomerAppreciationAward()));
    }

    @Override
    public boolean canSpawn() {
        return Loader.isModLoaded("anniv7")
                && anniv7 != null
                && cafeRoom != null
                && abstractCutscene != null
                && abstractNPC != null
                && abstractBartender != null
                && AbstractDungeon.actNum < 3;
    }

    public static final Trigger<Object> CAFE_ENTRY = new Trigger<>();
    public static final Trigger<Boolean> CAFE_DONE_WITH_NPC = new Trigger<>();

    @SpirePatch2(cls = "spireCafe.CafeRoom", method = "onEnterRoom", requiredModId = "anniv7")
    public static class CafeEntryTriggerPatch {
        @SpirePostfixPatch
        public static void cafeEntry(Object __instance) {
            CAFE_ENTRY.trigger(__instance);
        }
    }

    @SpirePatch2(cls = "spireCafe.abstracts.AbstractNPC", method = SpirePatch.CLASS, requiredModId = "anniv7")
    public static class CafeNPCFields {
        public static SpireField<Boolean> previousAlreadyPerformedTransaction = new SpireField<>(() -> false);
    }

    @SpirePatch2(cls = "spireCafe.abstracts.AbstractCutscene", method = "endCutscene", requiredModId = "anniv7")
    public static class EndCutsceneTrigger {
        @SpirePostfixPatch
        public static void endCutscene(Object __instance) {
            Object npc = ReflectionHacks.getPrivate(__instance, abstractCutscene, "character");
            boolean alreadyPerformedTransaction = ReflectionHacks.getPrivate(npc, abstractNPC, "alreadyPerformedTransaction");
            boolean previousAlreadyPerformedTransaction = CafeNPCFields.previousAlreadyPerformedTransaction.get(npc);
            if (alreadyPerformedTransaction && !previousAlreadyPerformedTransaction) {
                CafeNPCFields.previousAlreadyPerformedTransaction.set(npc, true);
                boolean isBartender = abstractBartender.isAssignableFrom(npc.getClass());
                CAFE_DONE_WITH_NPC.trigger(isBartender);
            }
        }
    }
}
