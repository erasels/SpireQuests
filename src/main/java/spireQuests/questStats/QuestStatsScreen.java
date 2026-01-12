package spireQuests.questStats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
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
import spireQuests.quests.Statistics;
import spireQuests.util.ImageHelper;
import spireQuests.util.TexLoader;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeUIPath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
    
    private static final Texture BG = TexLoader.getTexture(makeUIPath("stats/background.png"));
    private static final Texture BANNER_TOP = TexLoader.getTexture(makeUIPath("stats/banner_top.png"));
    private static final Texture BANNER_BOT = TexLoader.getTexture(makeUIPath("stats/banner_bottom.png"));
    private static final Texture BANNER_EXTRA = TexLoader.getTexture(makeUIPath("stats/banner_middle.png"));
    public static final Texture TROPHY_OUTLINE = TexLoader.getTexture(makeUIPath("stats/trophy/outline.png"));
    private static final Texture PROGRESS_BORDER = TexLoader.getTexture(makeUIPath("stats/progress_border.png"));
    private static final Texture PROGRESS_BAR = TexLoader.getTexture(makeUIPath("stats/progress_bar.png"));

    private static final float X_ANCHOR = 440.0F * Settings.xScale;
    private static final float Y_ANCHOR = (1080.0F - 195.0F) * Settings.yScale; // 885

    private static final float LEFT_ALIGN = X_ANCHOR + (25.0F * Settings.xScale);
    private static final float DROPDOWN_Y = Y_ANCHOR - (75.0F * Settings.yScale);

    private static final float BG_X = X_ANCHOR;
    private static final float BG_Y = 225.0F * Settings.yScale;
    
    private static final float BANNER_TOP_Y = Y_ANCHOR - (345.0F * Settings.yScale);
    private static final float BANNER_X = (X_ANCHOR + (BG.getWidth() * Settings.xScale))
        - (BANNER_TOP.getWidth() * Settings.scale / 2.0F)
        - 100.0F * Settings.xScale;

    private static final float QUEST_NAME_Y = Y_ANCHOR - (130.0F * Settings.yScale);
    private static final float QUEST_AUTHOR_Y = Y_ANCHOR - (170.0F * Settings.yScale);
    private static final float QUEST_DESCRIPTION_Y = Y_ANCHOR - (205.0F * Settings.yScale);
    private static final float QUEST_DESCRIPTION_LENGTH = 650.0F * Settings.xScale;

    private static final float ALL_QUEST_STAT_Y = Y_ANCHOR - (285.0F * Settings.yScale);
    private static final float QUEST_STAT_Y = Y_ANCHOR - (490.0F * Settings.yScale);

    private static final float REWARD_X = LEFT_ALIGN + (25.0F * Settings.scale);
    private static final float REWARD_OFFSET = 150.0F * Settings.scale;
    private static final float REWARD_Y = Y_ANCHOR - (375.0F * Settings.yScale);
    
    private static final float BADGE_X = BANNER_X + (53.0F * Settings.scale);
    private static final float BADGE_Y = Y_ANCHOR - (455.0F * Settings.yScale);
    private static final float BADGE_WIDTH = 100.0F * Settings.scale;
    private static final float BADGE_HEIGHT = 100.0F * Settings.scale;
    private static final int BADGES_PER_ROW = 3;

    private static final float TROPHY_Y = Y_ANCHOR - (335.0F * Settings.yScale);
    private static final float TROPHY_WIDTH = 218.0F * Settings.scale;
    private static final float TROPHY_HEIGHT = 265.0F * Settings.scale;

    private static final float TROPHY_HELP_X = BANNER_X + (55.0F * Settings.scale);
    private static final float TROPHY_HELP_Y = BANNER_TOP_Y + (275.0F * Settings.scale);
    private static final float TROPHY_HELP_LENGTH = 300.0F * Settings.scale;

    private static final float PROGRESS_BORDER_X = LEFT_ALIGN;
    private static final float PROGRESS_BORDER_Y = Y_ANCHOR - (275.0F * Settings.yScale);
    private static final float PROGRESS_TEXT_Y = Y_ANCHOR - (185.0F * Settings.yScale);

    private static final float PROGRESS_PADDING_X = 5.0F * Settings.xScale;
    private static final float PROGRESS_PADDING_Y = 5.0F * Settings.yScale;

    private static final float PROGRESS_BAR_X = PROGRESS_BORDER_X + PROGRESS_PADDING_X;
    private static final float PROGRESS_BAR_Y = PROGRESS_BORDER_Y + PROGRESS_PADDING_Y;
    private static final float PROGRESS_BAR_WIDTH = BANNER_X - PROGRESS_BAR_X - (50.0F * Settings.xScale);
    
    private static final float CHECKBOX_X = LEFT_ALIGN;
    private static final float CHECKBOX_Y = Y_ANCHOR - (632.0F * Settings.yScale);
    private static final float CHECKBOX_HEIGHT = 32.0F * Settings.scale;
    private static final float CHECKBOX_WIDTH = 32.0F * Settings.scale;
    

    private static final Color OUTLINE_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.33F);
    private static final Color LOCK_COLOR = Color.valueOf("#2d2d2d");
    private static final Color BRONZE_COLOR = Color.valueOf("#b65b2d");
    private static final Color SILVER_COLOR = Color.valueOf("#96969d");
    private static final Color GOLD_COLOR = Color.valueOf("#b07c21");
    
    private static final float FLASH_THRESH = 0.9F;
    private static final float FLASH_TIMER = 2.0F;

    private MenuCancelButton cancelButton = new MenuCancelButton();
    private DropdownMenu questDropdown;
    
    public static final String ID = makeID(QuestStatsScreen.class.getSimpleName());


    public static UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    private Collection<AbstractQuest> allQuests;
    private Map<String, AbstractQuest> allQuestsMap;
    private ArrayList<String> dropdownList;
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

    private float flashTimer = FLASH_TIMER;

    private Hitbox trophyHb;

    private float bannerBotDraw_y = 365.0F;

    private Hitbox checkboxHb;

    private boolean questEnabled;
    
    public QuestStatsScreen() {
        allQuests = QuestManager.getAllQuests();
        Statistics.removeExampleQuests(allQuests);
        allQuestsMap = allQuests.stream().collect(Collectors.toMap(q -> q.id, q -> q));
        nameIDMap = allQuests.stream().collect(Collectors.toMap(q -> q.name, q -> q.id));
        dropdownList = new ArrayList<>(allQuestsMap.values().stream().map(q -> q.name).collect(Collectors.toList()));
        dropdownList.sort(null);
        dropdownList.add(0, uiStrings.TEXT[5]);
        questDropdown = new DropdownMenu(this, dropdownList, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        selectedQuestStats = QuestStats.getAllStats();
        trophyHb = new Hitbox(0, 0, 0, 0);

        checkboxHb = new Hitbox(CHECKBOX_X, CHECKBOX_Y, CHECKBOX_WIDTH, CHECKBOX_HEIGHT);
        questEnabled = true;
        refreshData();
    }

    public void open() {
        CardCrawlGame.mainMenuScreen.screen = Enum.QUEST_STATS_SCREEN;
        CardCrawlGame.mainMenuScreen.darken();
        cancelButton.show(uiStrings.TEXT[6]);
        this.selectedQuest = null;
        selectedQuestStats = QuestStats.getAllStats();
        questDropdown = new DropdownMenu(this, dropdownList, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
        refreshData();

        Statistics.logStatistics(QuestManager.getAllQuests());
    }

    public void update() {
        if (this.flashTimer != 0.0F) {
            this.flashTimer -= Gdx.graphics.getDeltaTime();
            if (this.flashTimer < 0.0F) {
                this.flashTimer = FLASH_TIMER;
            }
        }

        if (questDropdown.isOpen) {
            questDropdown.update();
        } else {
            updateButtons();
            questDropdown.update();
            for (StatRewardBox box : rewardBoxes) {
                box.update();
            }
            this.trophyHb.update();
            if (this.trophyHb.hovered && (InputHelper.justClickedLeft)) {
                CardCrawlGame.sound.playAV("SHOVEL", MathUtils.random(0.6F, 0.9F), 0.5F);
            }
        }
    }

    private void updateButtons() {
        cancelButton.update();
        if (cancelButton.hb.clicked || InputHelper.pressedEscape) {
            InputHelper.pressedEscape = false;
            cancelButton.hb.clicked = false;
            CardCrawlGame.sound.play("DECK_CLOSE", 0.1F);
            cancelButton.hide();
            CardCrawlGame.mainMenuScreen.panelScreen.refresh();
        }

        checkboxHb.update();
        if (checkboxHb.hovered && InputHelper.justClickedLeft) {
            questEnabled = !questEnabled;
            QuestManager.setFilterConfig(selectedQuest.id, questEnabled);
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        renderBG(sb);
        if (selectedQuest == null){
            renderTrophyHelp(sb);
            renderSummary(sb);
        } else {
            renderTrophy(sb);
            renderStats(sb);
            renderRewards(sb);
            renderTrophyTooltip(sb);
            renderCheckbox(sb);
        }
        questDropdown.render(sb, LEFT_ALIGN, DROPDOWN_Y);
        cancelButton.render(sb);
    }

    private void renderBG(SpriteBatch sb) {
        sb.draw(BG, BG_X, BG_Y, BG.getWidth() * Settings.xScale, BG.getHeight() * Settings.scale);
        sb.draw(BANNER_TOP, BANNER_X, BANNER_TOP_Y, BANNER_TOP.getWidth() * Settings.scale, BANNER_TOP.getHeight() * Settings.scale);

        float midDraw = BANNER_TOP_Y;
        for (int i = 0; i < extraRows; i++) {
            midDraw -= (i + 1) * BANNER_EXTRA.getHeight() * Settings.scale;
            sb.draw(BANNER_EXTRA, BANNER_X, midDraw, BANNER_EXTRA.getWidth() * Settings.scale, BANNER_EXTRA.getHeight() * Settings.scale);
        }

        sb.draw(BANNER_BOT, BANNER_X, this.bannerBotDraw_y, BANNER_BOT.getWidth() * Settings.scale, BANNER_BOT.getHeight() * Settings.scale);
    }

    private void renderTrophy(SpriteBatch sb) {
        
        // float trophy_X = ((BADGE_X) + (BADGES_PER_ROW * BADGE_WIDTH) / 2.0F) - (TROPHY_WIDTH / 2.0F);
        float trophy_X = (BANNER_X + (BANNER_TOP.getWidth() * Settings.scale / 2.0F)) - (TROPHY_WIDTH / 2.0F);

        sb.setColor(OUTLINE_COLOR);
        sb.draw(TROPHY_OUTLINE, trophy_X, TROPHY_Y, TROPHY_WIDTH, TROPHY_HEIGHT);
        sb.setColor(Color.WHITE);
        sb.draw(selectedQuestStats.getTrophyTexture(), trophy_X, TROPHY_Y, TROPHY_WIDTH, TROPHY_HEIGHT);

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

    private void renderTrophyTooltip(SpriteBatch sb) {
        this.trophyHb.render(sb);
        if (this.trophyHb.hovered) {
            ImageHelper.tipBoxAtMousePos(selectedQuestStats.trophyTip.header, selectedQuestStats.trophyTip.body);
        }
    }

    private void renderSummary(SpriteBatch sb) {
        FontHelper.renderFont(sb, FontHelper.losePowerFont, uiStrings.TEXT[5], LEFT_ALIGN, QUEST_NAME_Y, Settings.CREAM_COLOR);

        renderProgressBarBG(sb, LOCK_COLOR, allQuests.size(), false);
        renderProgressBarBG(sb, BRONZE_COLOR, this.selectedQuestStats.bronzes, true);
        renderProgressBarBG(sb, SILVER_COLOR, this.selectedQuestStats.silvers, true);
        renderProgressBarBG(sb, GOLD_COLOR, this.selectedQuestStats.golds, true);

        sb.setColor(Color.WHITE);
        sb.draw(PROGRESS_BORDER, PROGRESS_BORDER_X, PROGRESS_BORDER_Y, PROGRESS_BAR_WIDTH, PROGRESS_BORDER.getHeight() * Settings.yScale);

        FontHelper.renderFont(sb, FontHelper.tipBodyFont, uiStrings.TEXT[8], LEFT_ALIGN, PROGRESS_TEXT_Y, Settings.CREAM_COLOR);

        strbuild.setLength(0);
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", 
            uiStrings.TEXT[9], selectedQuestStats.bronzes, allQuests.size(), 
            getPercent(selectedQuestStats.bronzes, allQuests.size()))
        );
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ",  
            uiStrings.TEXT[10], selectedQuestStats.silvers, allQuests.size(), 
            getPercent(selectedQuestStats.silvers, allQuests.size()))
        );
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ",  
            uiStrings.TEXT[11], selectedQuestStats.golds, allQuests.size(),
            getPercent(selectedQuestStats.golds, allQuests.size()))
        );
        
        FontHelper.renderSmartText(
            sb, FontHelper.tipBodyFont, strbuild.toString(), 
            LEFT_ALIGN, ALL_QUEST_STAT_Y, QUEST_DESCRIPTION_LENGTH,
            FontHelper.tipHeaderFont.getLineHeight(),
            Settings.CREAM_COLOR
        );
        strbuild.setLength(0);
        strbuild.append(String.format("%s: %d NL ", uiStrings.TEXT[12], timesSeen));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", uiStrings.TEXT[13], timesTaken, timesSeen, getPercent(timesTaken, timesSeen)));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", uiStrings.TEXT[14], timesCompleted, timesTaken, getPercent(timesCompleted, timesTaken)));
        strbuild.append(String.format("%s: %d/%d (%.2f%%) NL ", uiStrings.TEXT[15], timesFailed, timesTaken, getPercent(timesFailed, timesTaken)));
        
        FontHelper.renderSmartText(
            sb, FontHelper.tipBodyFont, strbuild.toString(), 
            LEFT_ALIGN, QUEST_STAT_Y, QUEST_DESCRIPTION_LENGTH,
            FontHelper.tipBodyFont.getLineHeight(),
            Settings.CREAM_COLOR
        );
    }

    private void renderProgressBarBG(SpriteBatch sb, Color color, int completed, boolean canFlash) {

        if (completed <= 0) {
            return;
        }

        float percent = (float) completed / (float) allQuests.size();
        float width = (PROGRESS_BAR_WIDTH * percent) - (PROGRESS_PADDING_X * 2.0F);

        sb.setColor(color);
        sb.draw(PROGRESS_BAR, PROGRESS_BAR_X, PROGRESS_BAR_Y, width, PROGRESS_BAR.getHeight() * Settings.yScale);

        if (canFlash && percent >= FLASH_THRESH) { //
            Color highlight = color.cpy().add(0.2F, 0.2F, 0.2F, 0.0F);
            highlight.a = this.flashTimer * 0.5f;
            sb.setColor(highlight);
            sb.draw(PROGRESS_BAR, PROGRESS_BAR_X, PROGRESS_BAR_Y, width, PROGRESS_BAR.getHeight() * Settings.yScale);
        }
    }

    private void renderStats(SpriteBatch sb) {
        
        FontHelper.renderFont(sb, FontHelper.losePowerFont, selectedQuest.name, LEFT_ALIGN, QUEST_NAME_Y, Settings.CREAM_COLOR);

        // Author
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, selectedQuest.author, 
            LEFT_ALIGN, QUEST_AUTHOR_Y, Settings.CREAM_COLOR
        );
        // Description
        FontHelper.renderSmartText(
            sb, FontHelper.cardDescFont_N, 
            selectedQuest.getDescription(), 
            LEFT_ALIGN, QUEST_DESCRIPTION_Y, QUEST_DESCRIPTION_LENGTH,
            FontHelper.cardDescFont_N.getLineHeight(),
            Settings.CREAM_COLOR
        );

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
        for (StatRewardBox box : rewardBoxes) {
            box.render(sb);
        }
    }

    private void renderCheckbox(SpriteBatch sb) {
        sb.draw(ImageMaster.OPTION_TOGGLE, CHECKBOX_X, CHECKBOX_Y, CHECKBOX_WIDTH, CHECKBOX_HEIGHT);
        Color textColor = Settings.CREAM_COLOR;
        if (this.checkboxHb.hovered) {
            textColor = Settings.BLUE_TEXT_COLOR;
        }
        FontHelper.renderFont(sb, FontHelper.tipBodyFont, uiStrings.TEXT[16], CHECKBOX_X + CHECKBOX_WIDTH
            , CHECKBOX_Y + (CHECKBOX_HEIGHT / 2.0F) + (FontHelper.getHeight(FontHelper.tipBodyFont) / 2.0F * Settings.scale)
            , textColor);

        if (questEnabled) {
            sb.setColor(Color.WHITE);
            sb.draw(ImageMaster.OPTION_TOGGLE_ON, CHECKBOX_X, CHECKBOX_Y, CHECKBOX_WIDTH, CHECKBOX_HEIGHT);
        }

        this.checkboxHb.render(sb);
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
            questEnabled = QuestManager.getFilterConfig(qid);
        }
        refreshData();
    }

    private void refreshData() {
        timesSeen = selectedQuestStats.timesSeen;
        timesTaken = selectedQuestStats.timesTaken;
        timesCompleted = selectedQuestStats.timesComplete;
        timesFailed = selectedQuestStats.timesFailed;
        
        extraRows = 0;
        this.bannerBotDraw_y = BANNER_TOP_Y - (BANNER_BOT.getHeight() * Settings.scale);
        rewardBoxes.clear();
        badgesToDraw.clear();

        this.trophyHb.resize(0.0F, 0.0F);
        this.trophyHb.move(-10000.0F, -10000.0F);

        this.checkboxHb.move(-10000.0F, -10000.0F);

        if (selectedQuest == null) {
            return;
        }

        HashSet<String> charactersCompletedAs = selectedQuestStats.charactersCompleted;
        extraRows = (charactersCompletedAs.size() - 1) / BADGES_PER_ROW;
        this.bannerBotDraw_y = (BANNER_TOP_Y - (extraRows * BANNER_EXTRA.getHeight() * Settings.scale)) - BANNER_BOT.getHeight()* Settings.scale;
        float bannerTotalHeight = (BANNER_TOP.getHeight() + BANNER_BOT.getHeight()) * Settings.scale;
        bannerTotalHeight += extraRows * BANNER_EXTRA.getHeight() * Settings.scale;
        this.trophyHb.resize(BANNER_TOP.getWidth() * Settings.scale, bannerTotalHeight);
        this.trophyHb.move(BANNER_X + BANNER_TOP.getWidth() * Settings.scale / 2.0F, this.bannerBotDraw_y + bannerTotalHeight / 2.0F);

        this.checkboxHb.move(CHECKBOX_X + CHECKBOX_WIDTH / 2.0F, CHECKBOX_Y + CHECKBOX_HEIGHT / 2.0F);

        for (AbstractPlayer chars : CardCrawlGame.characterManager.getAllCharacters()) {
            if (!charactersCompletedAs.contains(chars.chosenClass.toString())) {
                continue;
            }
            Texture button_texture = null;
            if (BaseMod.isBaseGameCharacter(chars)) {
                switch (chars.chosenClass) {
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
                button_texture = ImageMaster.loadImage(BaseMod.getPlayerButton(chars.chosenClass));
            }
            badgesToDraw.add(button_texture);
        }

        this.descriptionHeight = FontHelper.getSmartHeight(FontHelper.cardDescFont_N, selectedQuest.getDescription(),
                QUEST_DESCRIPTION_LENGTH, FontHelper.cardDescFont_N.getLineHeight()
            );
        this.descriptionHeight -= FontHelper.cardDescFont_N.getLineHeight();

        float yLine = ((QUEST_DESCRIPTION_Y + this.descriptionHeight) - (QUEST_STAT_Y + FontHelper.cardDescFont_N.getLineHeight())) / 2.0F;
        yLine = (QUEST_DESCRIPTION_Y + this.descriptionHeight) - (StatRewardBox.FRAME_Y / 2.0F) - yLine;

        if (yLine > REWARD_Y) {
            yLine = REWARD_Y;
        }

        float offset = 0.0f;

        rewardBoxes = selectedQuest.getStatRewardBoxes();
        for (StatRewardBox b : rewardBoxes) {
            b.move(REWARD_X + offset, yLine);
            offset += REWARD_OFFSET;
        }
        Collections.reverse(rewardBoxes);
    }

}
