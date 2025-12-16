package spireQuests.quests.iry.util;

import spireQuests.util.Wiz;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Simple utility class to determine the weight for each Lesson quest to sum to a total of 1
public class LessonQuestUtil {
    public static final Set<String> baseGameClassNames = new HashSet<>(Arrays.asList("IRONCLAD", "THE_SILENT", "DEFECT", "WATCHER"));
    public static float getLessonSpawnChance() {
        return baseGameClassNames.contains(Wiz.p().chosenClass.name()) ? 0.34f : 0.25f;
    }
}
