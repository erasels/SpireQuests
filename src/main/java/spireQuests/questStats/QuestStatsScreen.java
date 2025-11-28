package spireQuests.questStats;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;

import basemod.BaseMod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.quests.QuestReward;
import spireQuests.util.TexLoader;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeUIPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QuestStatsScreen implements DropdownMenuListener {
    
    public static class Enum {
        @SpireEnum
        public static MainMenuScreen.CurScreen QUEST_STATS_SCREEN;
    }

    private static final Logger logger = LogManager.getLogger(QuestStatsScreen.class.getName());

    private static final float X_ANCHOR = 440.0F * Settings.xScale;
    private static final float Y_ANCHOR = (1080.0F - 195.0F) * Settings.yScale; // 885

    private static final float LEFT_ALIGN = X_ANCHOR + (25.0F * Settings.xScale);
    private static final float DROPDOWN_Y = Y_ANCHOR - (75.0F * Settings.yScale);

    private static final float QUEST_NAME_Y = Y_ANCHOR - (130.0F * Settings.yScale);
    private static final float QUEST_AUTHOR_Y = Y_ANCHOR - (170.0F * Settings.yScale);
    private static final float QUEST_DESCRIPTION_Y = Y_ANCHOR - (205.0F * Settings.yScale);
    private static final float QUEST_DESCRIPTION_LENGTH = 650.0F * Settings.xScale;

    private static final float QUEST_STAT_Y = Y_ANCHOR - (525.0F * Settings.yScale);

    private static final float REWARD_X = LEFT_ALIGN + (35.0F * Settings.xScale);
    private static final float REWARD_OFFSET = 150.0F * Settings.xScale;
    private static final float REWARD_Y = Y_ANCHOR - (375.0F * Settings.yScale);

    private static final float BG_X = X_ANCHOR;
    private static final float BG_Y = 225.0F * Settings.yScale;

    private static final float BANNER_TOP_Y = Y_ANCHOR - (345.0F * Settings.yScale);
    private static final float BANNER_X = X_ANCHOR + (660.0F * Settings.xScale);

    private static final float BADGE_X = X_ANCHOR + (713.0F * Settings.xScale);
    private static final float BADGE_Y = Y_ANCHOR - (455.0F * Settings.yScale);
    private static final float BADGE_WIDTH = 100.0F * Settings.xScale;
    private static final float BADGE_HEIGHT = 100.0F * Settings.yScale;
    private static final int BADGES_PER_ROW = 3;

    private static final float TROPHY_Y = Y_ANCHOR - (335.0F * Settings.yScale);
    private static final float TROPHY_WIDTH = 218.0F * Settings.xScale;
    private static final float TROPHY_HEIGHT = 265.0F * Settings.yScale;

    private static final float TROPHY_HELP_X = BANNER_X + (55.0F * Settings.xScale);
    private static final float TROPHY_HELP_Y = BANNER_TOP_Y + (275.0F * Settings.yScale);
    private static final float TROPHY_HELP_LENGTH = 300.0F * Settings.xScale;

    private static final Texture BG = TexLoader.getTexture(makeUIPath("stats/background.png"));
    private static final Texture BANNER_TOP = TexLoader.getTexture(makeUIPath("stats/banner_top.png"));
    private static final Texture BANNER_BOT = TexLoader.getTexture(makeUIPath("stats/banner_bottom.png"));
    private static final Texture BANNER_EXTRA = TexLoader.getTexture(makeUIPath("stats/banner_middle.png"));
    private static final Texture TROPHY_OUTLINE = TexLoader.getTexture(makeUIPath("stats/trophy/outline.png"));
    private static final Texture TROPHY_HIDDEN = TexLoader.getTexture(makeUIPath("stats/trophy/locked.png"));
    private static final Texture TROPHY_BRONZE = TexLoader.getTexture(makeUIPath("stats/trophy/bronze.png"));
    private static final Texture TROPHY_SILVER = TexLoader.getTexture(makeUIPath("stats/trophy/silver.png"));
    private static final Texture TROPHY_GOLD = TexLoader.getTexture(makeUIPath("stats/trophy/gold.png"));
    
    private static final Color OUTLINE_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.33F);

    private MenuCancelButton cancelButton = new MenuCancelButton();
    private DropdownMenu questDropdown;
    
    public static final String ID = makeID(QuestStatsScreen.class.getSimpleName());
    public static UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    private Collection<AbstractQuest> allQuests;
    private Map<String, AbstractQuest> allQuestsMap;
    private ArrayList<String> allQuestList;
    private AbstractQuest selectedQuest;
    private QuestStats selectedQuestStats;
    private Map<String, String> nameIDMap;
    
    private int timesSeen = 0;
    private int timesTaken = 0;
    private int timesCompleted = 0;
    private int timesFailed = 0;
    private float descriptionHeight = 0.0f;
    
    private ArrayList<StatRewardBox> rewardBoxes = new ArrayList<>();
    private ArrayList<Texture> badgesToDraw = new ArrayList<>();

    private StringBuilder strbuild = new StringBuilder();

    private int extraRows;
    
    public QuestStatsScreen() {
        allQuests = QuestManager.getAllQuests();
        allQuestsMap = allQuests.stream().collect(Collectors.toMap(q -> q.id, q -> q));
        nameIDMap = allQuests.stream().collect(Collectors.toMap(q -> q.name, q -> q.id));
        allQuestList = new ArrayList<>(allQuestsMap.values().stream().map(q -> q.name).collect(Collectors.toList()));
        allQuestList.sort(null);
        allQuestList.add(0, uiStrings.TEXT[5]);
        questDropdown = new DropdownMenu(this, allQuestList, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        selectedQuestStats = QuestStats.getAllStats();
        refreshData();
    }

    public void open() {
        CardCrawlGame.mainMenuScreen.screen = Enum.QUEST_STATS_SCREEN;
        CardCrawlGame.mainMenuScreen.darken();
        cancelButton.show(uiStrings.TEXT[6]);
        this.selectedQuest = null;
        questDropdown = new DropdownMenu(this, allQuestList, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        refreshData();
    }

    public void update() {
        if (questDropdown.isOpen) {
            questDropdown.update();
        } else {
            updateButtons();
            questDropdown.update();
            for (StatRewardBox box : rewardBoxes) {
                box.update();
            }
        }
    }

    private void updateButtons() {
        cancelButton.update();
        if (cancelButton.hb.clicked || InputHelper.pressedEscape) {
            CardCrawlGame.mainMenuScreen.superDarken = false;
            InputHelper.pressedEscape = false;
            cancelButton.hb.clicked = false;
            cancelButton.hide();
            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
            CardCrawlGame.mainMenuScreen.lighten();
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        renderBG(sb);
        if (selectedQuest == null){
            renderTrophyHelp(sb);
        } else {
            renderTrophy(sb);
        }
        renderStats(sb);
        renderRewards(sb);
        questDropdown.render(sb, LEFT_ALIGN, DROPDOWN_Y);
        cancelButton.render(sb);
    }

    private void renderBG(SpriteBatch sb) {
        sb.draw(BG, BG_X, BG_Y, BG.getWidth() * Settings.xScale, BG.getHeight() * Settings.yScale);
        sb.draw(BANNER_TOP, BANNER_X, BANNER_TOP_Y, BANNER_TOP.getWidth() * Settings.xScale, BANNER_TOP.getHeight() * Settings.yScale);

        float midDraw = BANNER_TOP_Y;
        for (int i = 0; i < extraRows; i++) {
            midDraw -= (i + 1) * BANNER_EXTRA.getHeight() * Settings.yScale;
            sb.draw(BANNER_EXTRA, BANNER_X, midDraw, BANNER_EXTRA.getWidth() * Settings.xScale, BANNER_EXTRA.getHeight() * Settings.yScale);
        }

        float botDraw = midDraw - BANNER_BOT.getHeight()* Settings.yScale;
        sb.draw(BANNER_BOT, BANNER_X, botDraw, BANNER_BOT.getWidth() * Settings.xScale, BANNER_BOT.getHeight() * Settings.yScale);
    }

    private void renderTrophy(SpriteBatch sb) {
        
        float trophy_X = ((BADGE_X) + (BADGES_PER_ROW * BADGE_WIDTH) / 2.0F) - (TROPHY_WIDTH / 2.0F);

        sb.setColor(OUTLINE_COLOR);
        sb.draw(new TextureRegion(TROPHY_OUTLINE), trophy_X, TROPHY_Y, TROPHY_WIDTH, TROPHY_HEIGHT);
        sb.setColor(Color.WHITE);

        if (selectedQuestStats.timesComplete >= 25) {
            sb.draw(new TextureRegion(TROPHY_GOLD), trophy_X, TROPHY_Y, TROPHY_WIDTH, TROPHY_HEIGHT);
        } else if (selectedQuestStats.timesComplete >= 10) {
            sb.draw(new TextureRegion(TROPHY_SILVER), trophy_X, TROPHY_Y, TROPHY_WIDTH, TROPHY_HEIGHT);
        } else if (selectedQuestStats.timesComplete >= 1) {
            sb.draw(new TextureRegion(TROPHY_BRONZE), trophy_X, TROPHY_Y, TROPHY_WIDTH, TROPHY_HEIGHT);
        } else {
            sb.draw(new TextureRegion(TROPHY_HIDDEN), trophy_X, TROPHY_Y, TROPHY_WIDTH, TROPHY_HEIGHT);
        }

        for (int i = 0; i < badgesToDraw.size(); i++) {
            Texture t = badgesToDraw.get(i);

            int row = i / BADGES_PER_ROW;
            int col = i % BADGES_PER_ROW;

            int itemsInRow = Math.min(
                BADGES_PER_ROW, badgesToDraw.size() - (row * BADGES_PER_ROW)
            );

            float rowWidth = itemsInRow * BADGE_WIDTH;
            float xStart = BADGE_X + ((BADGES_PER_ROW * BADGE_WIDTH) - rowWidth) / 2.0F;

            float xDraw = xStart + (col * BADGE_WIDTH);
            float yDraw = BADGE_Y - row * (BADGE_HEIGHT);

            sb.draw(new TextureRegion(t), xDraw, yDraw, BADGE_WIDTH, BADGE_HEIGHT);
        }
    }

    private void renderTrophyHelp(SpriteBatch sb) {
        FontHelper.renderSmartText(
            sb, FontHelper.tipBodyFont, 
            uiStrings.TEXT[7], 
            TROPHY_HELP_X, TROPHY_HELP_Y, TROPHY_HELP_LENGTH,
            FontHelper.tipBodyFont.getLineHeight(),
            Settings.CREAM_COLOR
        );
    }

    private void renderStats(SpriteBatch sb) {
        String nameText;
        // Name
        if (selectedQuest == null) {
            nameText = uiStrings.TEXT[5];
        } else {
            nameText = selectedQuest.name;
        }
        FontHelper.renderFont(sb, FontHelper.losePowerFont, nameText, LEFT_ALIGN, QUEST_NAME_Y, Settings.CREAM_COLOR);

        // Quest Info
        if (selectedQuest == null) {
            // General Quest Info
        } else {
            // Author
            FontHelper.renderFont(sb, FontHelper.tipBodyFont, selectedQuest.author, 
                LEFT_ALIGN, QUEST_AUTHOR_Y, Settings.CREAM_COLOR
            );
            // Description
            FontHelper.renderSmartText(
                sb, FontHelper.cardDescFont_N, 
                selectedQuest.description, 
                LEFT_ALIGN, QUEST_DESCRIPTION_Y, QUEST_DESCRIPTION_LENGTH,
                FontHelper.cardDescFont_N.getLineHeight(),
                Settings.CREAM_COLOR
            );
            
        }
        // Stats
        strbuild.setLength(0);
        strbuild.append(String.format("%s: %d NL ", uiStrings.TEXT[1], timesSeen));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", uiStrings.TEXT[2], timesTaken, timesSeen, getPercent(timesTaken, timesSeen)));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", uiStrings.TEXT[3], timesCompleted, timesTaken, getPercent(timesCompleted, timesTaken)));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", uiStrings.TEXT[4], timesFailed, timesTaken, getPercent(timesFailed, timesTaken)));

        FontHelper.renderSmartText(
            sb, FontHelper.tipBodyFont, strbuild.toString(), 
            LEFT_ALIGN, QUEST_STAT_Y, QUEST_DESCRIPTION_LENGTH,
            FontHelper.tipBodyFont.getLineHeight(),
            Settings.CREAM_COLOR
        );
    }

    private float getPercent(int num, int den) {
        if (den == 0) {
            return 0.0f;
        }
        return (num * 100.0f)/den;
    }

    private void renderRewards(SpriteBatch sb) {
        if (selectedQuest == null) {
            return;
        }
        for (StatRewardBox box : rewardBoxes) {
            box.render(sb);
        }
    }

    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        if (i == 0) {
            selectedQuestStats = QuestStats.getAllStats();
            selectedQuest = null;
        } else {
            String qid = nameIDMap.get(s);
            selectedQuestStats = new QuestStats(qid);
            selectedQuest = allQuestsMap.get(qid);
        }
        refreshData();
    }

    private void refreshData() {
        timesSeen = selectedQuestStats.timesSeen;
        timesTaken = selectedQuestStats.timesTaken;
        timesCompleted = selectedQuestStats.timesComplete;
        timesFailed = selectedQuestStats.timesFailed;
        
        extraRows = 0;
        rewardBoxes.clear();
        badgesToDraw.clear();

        if (selectedQuest == null) {
            return;
        }

        ArrayList<String> charactersCompletedAs = selectedQuestStats.charactersCompleted;
        extraRows = (charactersCompletedAs.size() - 1) / BADGES_PER_ROW;

        for (String s : charactersCompletedAs) {
            try {
                PlayerClass playerClass = AbstractPlayer.PlayerClass.valueOf(s);
                Texture button_texture = null;
                if (BaseMod.isBaseGameCharacter(playerClass)) {
                    switch (playerClass) {
                        case IRONCLAD:
                            button_texture = ImageMaster.CHAR_SELECT_IRONCLAD;
                            break;
                        case THE_SILENT:
                            button_texture = ImageMaster.CHAR_SELECT_SILENT;
                            break;
                        case DEFECT:
                            button_texture = ImageMaster.CHAR_SELECT_DEFECT;
                            break;
                        case WATCHER:
                            button_texture = ImageMaster.CHAR_SELECT_WATCHER;
                            break;
                    }
                } else {
                    button_texture = ImageMaster.loadImage(BaseMod.getPlayerButton(playerClass));
                }
                badgesToDraw.add(button_texture);
            } catch (Exception e) {
                logger.error("Error loading " + s + " character. Skipping for quest trophy data.");
            }
        }

        this.descriptionHeight = FontHelper.getSmartHeight(FontHelper.cardDescFont_N, selectedQuest.description,
                QUEST_DESCRIPTION_LENGTH, FontHelper.cardDescFont_N.getLineHeight()
            );
        this.descriptionHeight -= FontHelper.cardDescFont_N.getLineHeight();

        float yLine = ((QUEST_DESCRIPTION_Y + this.descriptionHeight) - (QUEST_STAT_Y + FontHelper.cardDescFont_N.getLineHeight())) / 2.0F;
        yLine = (QUEST_DESCRIPTION_Y + this.descriptionHeight) - (StatRewardBox.FRAME_Y / 2.0F) - yLine;

        if (yLine > REWARD_Y) {
            yLine = REWARD_Y;
        }

        float offset = 0.0f;
        if (selectedQuest.useDefaultReward) { // I thought this var was for if you had custom rewards or used addRewards. I was incorrect.
            rewardBoxes.add(new StatRewardBox(selectedQuest, REWARD_X, yLine));
        } else {
            for (QuestReward r : selectedQuest.questRewards) {
                rewardBoxes.add(new StatRewardBox(r, REWARD_X + offset, yLine));
                offset += REWARD_OFFSET;
            }
        }
        Collections.reverse(rewardBoxes);
    }

}
