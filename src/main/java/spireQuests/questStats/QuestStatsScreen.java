package spireQuests.questStats;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.options.DropdownMenu;
import com.megacrit.cardcrawl.screens.options.DropdownMenuListener;

import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QuestStatsScreen implements DropdownMenuListener {
    
    public static class Enum {
        @SpireEnum
        public static MainMenuScreen.CurScreen QUEST_STATS_SCREEN;
    }

    private static final Logger logger = LogManager.getLogger(MainMenuScreen.class.getName());
    private static final float DROPDOWN_X = 20.0F * Settings.scale;
    private static final float DROPDOWN_Y = Settings.HEIGHT/2f + 340.0F * Settings.scale;
    private MenuCancelButton cancelButton = new MenuCancelButton();
    private DropdownMenu questDropdown;
    private Map<String, String> allQuests;
    private String selectedQuestID;
    
    public QuestStatsScreen() {
        allQuests = QuestManager.getAllQuests().stream().collect(Collectors.toMap(q -> q.name, q -> q.id));
        ArrayList<String> allQuestList = new ArrayList<>(allQuests.keySet());
        allQuestList.sort(null);
        allQuestList.add(0, "TEXT[ALL_QUESTS]");
        questDropdown = new DropdownMenu(this, allQuestList, FontHelper.tipBodyFont, Settings.CREAM_COLOR);
    }

    public void open() {
        CardCrawlGame.mainMenuScreen.screen = Enum.QUEST_STATS_SCREEN;
        CardCrawlGame.mainMenuScreen.darken();
        cancelButton.show("TEXT[CANCEL]");
    }

    public void update() {
        if (questDropdown.isOpen) {
            questDropdown.update();
        } else {
            updateButtons();
            questDropdown.update();
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
        questDropdown.render(sb, DROPDOWN_X, DROPDOWN_Y);
        cancelButton.render(sb);
    }

    @Override
    public void changedSelectionTo(DropdownMenu dropdownMenu, int i, String s) {
        if (i == 0) {
            selectedQuestID = null;
        } else {
            selectedQuestID = allQuests.get(s);
        }
        // refreshPage();
    }
}
