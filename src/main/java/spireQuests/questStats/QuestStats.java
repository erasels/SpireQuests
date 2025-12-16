package spireQuests.questStats;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeUIPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.UIStrings;

import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.Statistics;
import spireQuests.util.TexLoader;

public class QuestStats {    

    public static final String ID = makeID(QuestStats.class.getSimpleName());
    private static UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    public static final Texture SMALL_TROPHY_HIDDEN = TexLoader.getTexture(makeUIPath("stats/trophy/small/locked.png"));
    public static final Texture SMALL_TROPHY_BRONZE = TexLoader.getTexture(makeUIPath("stats/trophy/small/bronze.png"));
    public static final Texture SMALL_TROPHY_SILVER = TexLoader.getTexture(makeUIPath("stats/trophy/small/silver.png"));
    public static final Texture SMALL_TROPHY_GOLD = TexLoader.getTexture(makeUIPath("stats/trophy/small/gold.png"));

    public static final Texture TROPHY_OUTLINE = TexLoader.getTexture(makeUIPath("stats/trophy/outline.png"));
    public static final Texture TROPHY_HIDDEN = TexLoader.getTexture(makeUIPath("stats/trophy/locked.png"));
    public static final Texture TROPHY_BRONZE = TexLoader.getTexture(makeUIPath("stats/trophy/bronze.png"));
    public static final Texture TROPHY_SILVER = TexLoader.getTexture(makeUIPath("stats/trophy/silver.png"));
    public static final Texture TROPHY_GOLD = TexLoader.getTexture(makeUIPath("stats/trophy/gold.png"));

    public enum TrophyLevel {
        NONE,
        BRONZE,
        SILVER,
        GOLD
    }

    public static int BRONZE_THRESH = 1;
    public static int SILVER_THRESH = 5;
    public static int GOLD_THRESH = 10;

    public int timesSeen = 0;
    public int timesTaken = 0;
    public int timesComplete = 0;
    public int timesFailed = 0;
    public HashSet<String> charactersCompleted = new HashSet<>();

    // We're using ints and not booleans here because
    // AllQuestStats is just a QuestStats object as well.
    public int bronzes = 0;
    public int silvers = 0;
    public int golds = 0;

    public TrophyLevel trophyType;
    public PowerTip trophyTip;

    private QuestStats() {}

    public QuestStats(AbstractQuest quest) {
        this(quest.id);
    }

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

        this.trophyType = getTrophy();
        createTrophyTips();
    }

    private void createTrophyTips() {
        this.trophyTip = new PowerTip();
        this.trophyTip.header = uiStrings.TEXT[0];
        StringBuilder strbuild = new StringBuilder();
        switch (this.trophyType) {
            case BRONZE:
                strbuild.append(uiStrings.TEXT[1]);
                break;
            case SILVER:
                strbuild.append(uiStrings.TEXT[2]);
                break;
            case GOLD:
                strbuild.append(uiStrings.TEXT[3]);
                break;
            case NONE:
                strbuild.append(uiStrings.TEXT[4]);
                break;
        }

        
        if (!this.charactersCompleted.isEmpty()) {
            ArrayList<String> localizedChars = new ArrayList<>();
            for (AbstractPlayer chars : CardCrawlGame.characterManager.getAllCharacters()) {
                if (!this.charactersCompleted.contains(chars.chosenClass.toString())) {
                    continue;
                }
                localizedChars.add(chars.getLocalizedCharacterName());
            }

            strbuild.append(uiStrings.TEXT[5]);
            strbuild.append(String.join(", ", localizedChars));
        }
        this.trophyTip.body = strbuild.toString();
    }

    public boolean hasBadge(PlayerClass playerClass) {
        return this.charactersCompleted.contains(playerClass.toString());
    }

    public boolean hasBronze() {
        return this.bronzes >= 1;
    }

    public boolean hasSilver() {
        return this.silvers >= 1;
    }

    public boolean hasGold() {
        return this.golds >= 1;
    }

    public TrophyLevel getTrophy() {
        if (this.golds >= 1) {
            return TrophyLevel.GOLD;
        } else if (this.silvers >= 1) {
            return TrophyLevel.SILVER;
        } else if (this.bronzes >= 1) {
             return TrophyLevel.BRONZE;
        } else {
            return TrophyLevel.NONE;
        }
    }

    public Texture getTrophyTexture() {
        switch (this.trophyType) {
            case BRONZE:
                return TROPHY_BRONZE;
            case SILVER:
                return TROPHY_SILVER;
            case GOLD:
                return TROPHY_GOLD;
            case NONE:
                return TROPHY_HIDDEN;
            default:
                return TROPHY_HIDDEN;
        }
    }

    public Texture getSmallTrophyTexture() {
        switch (this.trophyType) {
            case BRONZE:
                return SMALL_TROPHY_BRONZE;
            case SILVER:
                return SMALL_TROPHY_SILVER;
            case GOLD:
                return SMALL_TROPHY_GOLD;
            case NONE:
                return SMALL_TROPHY_HIDDEN;
            default:
                return SMALL_TROPHY_HIDDEN;
        }
    }

    public static QuestStats getAllStats() {
        Collection<AbstractQuest> allQuests = QuestManager.getAllQuests();
        Statistics.removeExampleQuests(allQuests);
        List<String> allIDs = allQuests.stream().map(q -> q.id).collect(Collectors.toList());
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

    public static boolean hasFinishedAnyQuest() {
        QuestStats qs = getAllStats();
        return qs.timesComplete > 0 || qs.timesFailed > 0;
    }
}
