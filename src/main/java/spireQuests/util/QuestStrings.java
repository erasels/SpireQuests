package spireQuests.util;

public class QuestStrings {
    public String TITLE;
    public String DESCRIPTION;
    public String AUTHOR;
    public String REWARD = null;
    public String[] TRACKER_TEXT = new String[]{};
    public String[] EXTRA_TEXT = new String[]{};

    public QuestStrings() {}

    private QuestStrings(
            String title,
            String desc,
            String author,
            String reward,
            String[] tracker
    ) {
        TITLE = title;
        DESCRIPTION = desc;
        AUTHOR = author;
        REWARD = reward;
        TRACKER_TEXT = tracker;
        EXTRA_TEXT = new String[]{};
    }

    public static QuestStrings DEFAULT = new QuestStrings(
            "[none]",
            "[none]",
            "[none]",
            "[none]",
            new String[]{"[none]"}
    );
}
