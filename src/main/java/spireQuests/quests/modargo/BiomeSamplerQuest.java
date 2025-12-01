package spireQuests.quests.modargo;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.Trigger;

import java.util.Collections;

import static spireQuests.util.CompatUtil.pmLoaded;

public class BiomeSamplerQuest extends AbstractQuest {
    public static Class<?> anniv6;
    public static Class<?> zonePatches;
    public static Class<?> travelTrackingPatches;

    public BiomeSamplerQuest() {
        super(QuestType.LONG, QuestDifficulty.EASY);
        if (Loader.isModLoaded("anniv6")) {
            try {
                anniv6 = Class.forName("spireMapOverhaul.SpireAnniversary6Mod");
                zonePatches = Class.forName("spireMapOverhaul.patches.ZonePatches");
                travelTrackingPatches = Class.forName("spireMapOverhaul.patches.interfacePatches.TravelTrackingPatches$NextRoomTravel");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Error retrieving classes from Spire Biomes", e);
            }
        }

        new TriggerTracker<>(ENTER_BIOME, 5)
                .add(this);

        addReward(new QuestReward.MaxHPReward(5));
    }

    @Override
    public boolean canSpawn() {
        return Loader.isModLoaded("anniv6")
                && anniv6 != null
                && zonePatches != null
                && travelTrackingPatches != null
                && biomesActive()
                && getMinZonesPerAct() == 3 // The default, different values make the quest too easy or too hard
                && AbstractDungeon.actNum == 1;
    }

    private boolean biomesActive() {
        return ReflectionHacks.getPrivateStatic(anniv6, "currentRunActive");
    }

    private int getMinZonesPerAct() {
        return ReflectionHacks.privateMethod(anniv6, "getZoneCountIndex").invoke(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        Object currentZone = ReflectionHacks.privateMethod(zonePatches, "currentZone").invoke(null);
        // If you're in a biome when you start this quest, count it as visited
        if (currentZone != null) {
            ENTER_BIOME.trigger();
        }
    }

    // Like Spire Biomes itself, this counts each entry if you leave and re-enter a zone by using Wing Boots or similar
    public static final Trigger<Void> ENTER_BIOME = new Trigger<>();

    @SpirePatch2(cls = "spireMapOverhaul.patches.interfacePatches.TravelTrackingPatches$NextRoomTravel", method = "onEnterNextRoom", requiredModId = "anniv6")
    public static class EnterBiomeTriggerPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void enterBiomeTrigger() {
            ENTER_BIOME.trigger();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher matcher = new Matcher.MethodCallMatcher("spireMapOverhaul.patches.interfacePatches.TravelTrackingPatches$NextRoomTravel", "leftZone");
                return LineFinder.findInOrder(ctMethodToPatch, Collections.singletonList(matcher), matcher);
            }
        }
    }
}
