package spireQuests.questStats;

import java.util.ArrayList;

import com.megacrit.cardcrawl.screens.stats.RunData;

public class QuestStats {
    private ArrayList<RunData> runs = new ArrayList<>();
    private int timesSeen;
    private int timesTaken;
    private int timesComplete;
    private int timesFailed;

    public QuestStats(ArrayList<QuestStats> allQuests) {
        timesSeen = 0;
        timesTaken = 0;
        timesComplete = 0;
        timesFailed = 0;
    }
    
}
