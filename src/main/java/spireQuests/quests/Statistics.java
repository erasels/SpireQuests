package spireQuests.quests;

import spireQuests.Anniv8Mod;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistics {
    public static void logStatistics(Collection<AbstractQuest> quests) {
        removeExampleQuests(quests);
        Map<AbstractQuest.QuestDifficulty, List<AbstractQuest>> questsByDifficulty = quests.stream().collect(Collectors.groupingBy(q -> q.difficulty));
        Anniv8Mod.logger.info(String.format("Quest difficulty: Easy: %s, Normal: %s, Hard: %s, Challenge: %s, Total: %s",
                questsByDifficulty.get(AbstractQuest.QuestDifficulty.EASY).size(),
                questsByDifficulty.get(AbstractQuest.QuestDifficulty.NORMAL).size(),
                questsByDifficulty.get(AbstractQuest.QuestDifficulty.HARD).size(),
                questsByDifficulty.get(AbstractQuest.QuestDifficulty.CHALLENGE).size(),
                quests.size()
        ));
        Map<AbstractQuest.QuestType, List<AbstractQuest>> questsByLength = quests.stream().collect(Collectors.groupingBy(q -> q.type));
        Anniv8Mod.logger.info(String.format("Quest length: Short: %s, Long: %s",
                questsByLength.get(AbstractQuest.QuestType.SHORT).size(),
                questsByLength.get(AbstractQuest.QuestType.LONG).size()
        ));
        Map<String, List<AbstractQuest>> questsByAuthor = quests.stream().collect(Collectors.groupingBy(q -> q.author));
        Anniv8Mod.logger.info("Quest author: " + questsByAuthor.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue().size()).collect(Collectors.joining(", ")));

        int generic = 0;
        int specificRelic = 0;
        int randomRelic = 0;
        int card = 0;
        int gold = 0;
        int potion = 0;
        int custom = 0;
        int multipleTypes = 0;
        int multipleItems = 0;
        for (AbstractQuest q : quests) {
            List<QuestReward> rs = q.questRewards;
            if (q.useDefaultReward) {
                generic++;
                if (rs.size() > 1){
                    multipleItems++;
                }
                continue;
            }
            if (rs.stream().anyMatch(r -> r instanceof QuestReward.RelicReward)) {
                specificRelic++;
            }
            if (rs.stream().anyMatch(r -> r instanceof QuestReward.RandomRelicReward)) {
                randomRelic++;
            }
            if (rs.stream().anyMatch(r -> r instanceof QuestReward.CardReward)) {
                card++;
            }
            if (rs.stream().anyMatch(r -> r instanceof QuestReward.GoldReward)) {
                gold++;
            }
            if (rs.stream().anyMatch(r -> r instanceof QuestReward.PotionReward)) {
                potion++;
            }
            if (rs.stream().map(QuestReward::getClass).distinct().count() > 1) {
                multipleTypes++;
            }
            if (rs.size() > 1) {
                multipleItems++;
            }
            if (rs.isEmpty()) {
                custom++;
            }
        }
        Anniv8Mod.logger.info(String.format("Quest rewards: relic: %s, random relic: %s, card: %s, gold: %s, potion: %s, generic: %s, custom: %s, multiple types: %s, multiple items: %s",
                specificRelic, randomRelic, card, gold, potion, generic, custom, multipleTypes, multipleItems));
    }

    public static void removeExampleQuests(Collection<AbstractQuest> quests) {
        quests.removeIf(q -> q.getClass().getName().contains(".example."));
    }
}