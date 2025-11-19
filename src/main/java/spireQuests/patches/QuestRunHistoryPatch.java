package spireQuests.patches;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomSavable;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.metrics.Metrics;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryPath;
import com.megacrit.cardcrawl.screens.runHistory.RunPathElement;
import com.megacrit.cardcrawl.screens.stats.RunData;
import javassist.*;
import org.apache.logging.log4j.Logger;
import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpirePatch(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
public class QuestRunHistoryPatch {
    private static final Logger logger = Anniv8Mod.logger;
    private static final String[] QUEST_TEXT = CardCrawlGame.languagePack.getUIString(Anniv8Mod.makeID("RunHistoryScreen")).TEXT;

    public static SpireField<List<List<String>>> questPickupPerFloorLog = new SpireField<>(ArrayList::new);
    public static SpireField<List<List<String>>> questCompletionPerFloorLog = new SpireField<>(ArrayList::new);

    public static void initialize() {
        BaseMod.addSaveField(Anniv8Mod.makeID("QuestPickupPerFloor"), new CustomSavable<List<List<String>>>() {
            @Override
            public List<List<String>> onSave() {
                return questPickupPerFloorLog.get(AbstractDungeon.player);
            }

            @Override
            public void onLoad(List<List<String>> list) {
                if (list == null) {
                    return;
                }
                questPickupPerFloorLog.get(AbstractDungeon.player).addAll(list);
            }
        });
        BaseMod.addSaveField(Anniv8Mod.makeID("QuestCompletionPerFloor"), new CustomSavable<List<List<String>>>() {
            @Override
            public List<List<String>> onSave() {
                return questCompletionPerFloorLog.get(AbstractDungeon.player);
            }

            @Override
            public void onLoad(List<List<String>> list) {
                if (list == null) {
                    return;
                }
                questCompletionPerFloorLog.get(AbstractDungeon.player).addAll(list);
            }
        });
    }

    @SpirePatch(clz = CardCrawlGame.class, method = SpirePatch.CONSTRUCTOR)
    public static class AddQuestFieldsPatch {
        @SpireRawPatch
        public static void addQuestFields(CtBehavior ctBehavior) throws NotFoundException, CannotCompileException {
            CtClass runData = ctBehavior.getDeclaringClass().getClassPool().get(RunData.class.getName());

            String fieldSource1 = "public java.util.List quest_pickup_per_floor;";
            CtField field1 = CtField.make(fieldSource1, runData);
            runData.addField(field1);

            String fieldSource2 = "public java.util.List quest_completion_per_floor;";
            CtField field2 = CtField.make(fieldSource2, runData);
            runData.addField(field2);
        }
    }

    @SpirePatch(clz = Metrics.class, method = "gatherAllData")
    public static class GatherAllDataPatch {
        @SpirePostfixPatch
        public static void gatherAllDataPatch(Metrics __instance, boolean death, boolean trueVictor, MonsterGroup monsters) {
            ReflectionHacks.privateMethod(Metrics.class, "addData", Object.class, Object.class)
                    .invoke(__instance, "quest_pickup_per_floor", questPickupPerFloorLog.get(AbstractDungeon.player));
            ReflectionHacks.privateMethod(Metrics.class, "addData", Object.class, Object.class)
                    .invoke(__instance, "quest_completion_per_floor", questCompletionPerFloorLog.get(AbstractDungeon.player));
        }
    }

    @SpirePatch(clz = RunPathElement.class, method = SpirePatch.CLASS)
    public static class RunPathElementFields {
        public static final SpireField<List<String>> questPickups = new SpireField<>(() -> null);
        public static final SpireField<List<String>> questCompletions = new SpireField<>(() -> null);
    }

    @SpirePatch(clz = RunHistoryPath.class, method = "setRunData")
    public static class AddQuestDataPatch {
        @SuppressWarnings("rawtypes")
        @SpireInsertPatch(locator = Locator.class, localvars = {"element", "i"})
        public static void addQuestData(RunHistoryPath __instance, RunData newData, RunPathElement element, int i) throws NoSuchFieldException, IllegalAccessException {
            Field field1 = newData.getClass().getField("quest_pickup_per_floor");
            Field field2 = newData.getClass().getField("quest_completion_per_floor");
            List quest_pickup_per_floor = (List) field1.get(newData);
            List quest_completion_per_floor = (List) field2.get(newData);
            // Element 0 of the quest data is what happened in the Neow room, but there's no RunPathElement for the Neow
            // room, so we show quests picked up from Neow on the first floor.
            // This means we ignore element 1 of the quest pickup data and element 0 of the quest completion data. This
            // is fine, because we assume the player can't get quests on the first floor since it's always a combat and
            // can't complete quests in the Neow room since that would be a trivial quest.
            int pickupDataIndex = i == 0 ? 0 : i + 1;
            if (quest_pickup_per_floor != null && pickupDataIndex < quest_pickup_per_floor.size()) {
                Object questIDs = quest_pickup_per_floor.get(pickupDataIndex);
                List<String> s = null;
                if (questIDs instanceof List) {
                    //noinspection unchecked
                    s = (List<String>) questIDs;
                } else if (questIDs != null) {
                    logger.warn("Unrecognized quest_pickup_per_floor data: " + questIDs);
                }
                RunPathElementFields.questPickups.set(element, s);
            }
            if (quest_completion_per_floor != null && i + 1 < quest_completion_per_floor.size()) {
                Object questIDs = quest_completion_per_floor.get(i + 1);
                List<String> s = null;
                if (questIDs instanceof List) {
                    //noinspection unchecked
                    s = (List<String>) questIDs;
                } else if (questIDs != null) {
                    logger.warn("Unrecognized quest_completion_per_floor data: " + questIDs);
                }
                RunPathElementFields.questCompletions.set(element, s);
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.NewExprMatcher(RunPathElement.class);
                Matcher finalMatcher = new Matcher.MethodCallMatcher(List.class, "add");
                return LineFinder.findInOrder(ctMethodToPatch, Collections.singletonList(matcher), finalMatcher);
            }
        }
    }

    @SpirePatch(clz = RunPathElement.class, method = "getTipDescriptionText")
    public static class DisplayQuestDataPatch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"sb"})
        public static void displayQuestData(RunPathElement __instance, StringBuilder sb) {
            List<String> questPickups = RunPathElementFields.questPickups.get(__instance);
            List<String> questCompletions = RunPathElementFields.questCompletions.get(__instance);
            Map<String, AbstractQuest> allQuests = QuestManager.getAllQuests().stream().collect(Collectors.toMap(q -> q.id, q -> q));
            if (questPickups != null && !questPickups.isEmpty()) {
                String questNames = questPickups.stream()
                        .map(id -> allQuests.getOrDefault(id, null))
                        .filter(q -> q != null)
                        .map(q -> q.name)
                        .map(s -> FontHelper.colorString(s, "b"))
                        .collect(Collectors.joining(" NL TAB "));
                if (!questNames.isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append(" NL ");
                    }
                    sb.append(FontHelper.colorString(QUEST_TEXT[0], "y"));
                    sb.append(" NL TAB ");
                    sb.append(questNames);
                }
            }
            if (questCompletions != null && !questCompletions.isEmpty()) {
                String questNames = questCompletions.stream()
                        .map(id -> allQuests.getOrDefault(id, null))
                        .filter(q -> q != null)
                        .map(q -> q.name)
                        .map(s -> FontHelper.colorString(s, "b"))
                        .collect(Collectors.joining(" NL TAB "));
                if (!questNames.isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append(" NL ");
                    }
                    sb.append(FontHelper.colorString(QUEST_TEXT[1], "y"));
                    sb.append(" NL TAB ");
                    sb.append(questNames);
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher matcher = new Matcher.FieldAccessMatcher(RunPathElement.class, "shopPurchases");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }

    @SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
    public static class NextRoomTransitionAddEntriesPatch {
        @SpireInsertPatch(locator = Locator.class)
        public static void nextRoomTransitionAddEntriesPatch(AbstractDungeon __instance, SaveFile saveFile) {
            questPickupPerFloorLog.get(AbstractDungeon.player).add(new ArrayList<>());
            questCompletionPerFloorLog.get(AbstractDungeon.player).add(new ArrayList<>());
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "relics");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }

    // You can get quests in the Neow room and nextRoomTransition isn't called for that, so we need to handle it separately
    @SpirePatch(clz = BaseMod.class, method = "publishStartGame")
    public static class StartOfRunPatch {
        @SpirePostfixPatch
        public static void startOfRun() {
            if (!CardCrawlGame.loadingSave) {
                questPickupPerFloorLog.get(AbstractDungeon.player).add(new ArrayList<>());
                questCompletionPerFloorLog.get(AbstractDungeon.player).add(new ArrayList<>());
            }
        }
    }
}
