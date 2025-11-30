package spireQuests.questStats;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;

import spireQuests.quests.QuestManager;

public class QuestStats {    

    public static int BRONZE_THRESH = 1;
    public static int SILVER_THRESH = 5;
    public static int GOLD_THRESH = 10;

    public int timesSeen = 0;
    public int timesTaken = 0;
    public int timesComplete = 0;
    public int timesFailed = 0;
    public ArrayList<String> charactersCompleted = new ArrayList<>();

    // We're using ints and not booleans here because
    // AllQuestStats is just a QuestStats object as well.
    public int bronzes = 0;
    public int silvers = 0;
    public int golds = 0;

    public QuestStats() {}

    public QuestStats(String qid) {
        JsonObject statObj = QuestStatManager.getStatsForQuest(qid);
        this.timesSeen = statObj.get(QuestStatManager.SEEN).getAsInt();
        this.timesTaken = statObj.get(QuestStatManager.TAKEN).getAsInt();
        this.timesComplete = statObj.get(QuestStatManager.COMPLETED).getAsInt();
        this.timesFailed = statObj.get(QuestStatManager.FAILED).getAsInt();
        JsonArray jsonArray = statObj.get(QuestStatManager.CHARACTERS).getAsJsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            this.charactersCompleted.add(jsonArray.get(i).getAsString());
        }

        if (this.timesComplete >= BRONZE_THRESH){
            this.bronzes = 1;
        }
        if (this.timesComplete >= SILVER_THRESH){
            this.silvers = 1;
        }
        if (this.timesComplete >= GOLD_THRESH){
            this.golds = 1;
        }
    }

    public boolean hasBadge(PlayerClass playerClass) {
        if (this.charactersCompleted.contains(playerClass.toString())) {
            return true;
        }
        return false;
    }

    public boolean hasBronze() {
        if (this.bronzes >= 1) {
            return true;
        }
        return false;
    }

    public boolean hasSilver() {
        if (this.silvers >= 1) {
            return true;
        }
        return false;
    }

    public boolean hasGold() {
        if (this.golds >= 1) {
            return true;
        }
        return false;
    }

    public static QuestStats getAllStats() {
        List<String> allIDs = QuestManager.getAllQuests().stream().map(q -> q.id).collect(Collectors.toList());
        List<QuestStats> allStats = new ArrayList<>();
        for (String q : allIDs) {
            allStats.add(new QuestStats(q));
        }
        QuestStats ret = new QuestStats();
        ret.timesSeen = allStats.stream().mapToInt(s -> s.timesSeen).sum();
        ret.timesTaken = allStats.stream().mapToInt(s -> s.timesTaken).sum();
        ret.timesComplete = allStats.stream().mapToInt(s -> s.timesComplete).sum();
        ret.timesFailed = allStats.stream().mapToInt(s -> s.timesFailed).sum();

        ret.bronzes = (int) allStats.stream().mapToInt(s -> s.bronzes).sum();
        ret.silvers = (int) allStats.stream().mapToInt(s -> s.silvers).sum();
        ret.golds = (int) allStats.stream().mapToInt(s -> s.golds).sum();
        return ret;
    }
}
