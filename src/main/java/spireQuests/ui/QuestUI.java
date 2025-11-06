package spireQuests.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.localization.UIStrings;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.util.ImageHelper;
import spireQuests.util.TexLoader;

import java.util.List;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeUIPath;

/**
 * Will probably position it top right under game info
 * click to collapse/uncollapse
 * when a quest is complete exclamation point at top and next to completed quests if un-collapsed
 * colored quest name based on difficulty, objectives are all white but turn yellow when complete
 * quest description displayed only when obtaining quest and when hovering over quest
 * on ui just show name and trackers
 */

public class QuestUI {
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("QuestUI"));
    private static final String[] TEXT = uiStrings.TEXT;

    private static final float LARGE_SPACING = 34; //no settings.scale for text readability
    private static final float SMALL_SPACING = 24;

    private static final BitmapFont largeFont = FontHelper.cardTitleFont;
    private static final BitmapFont smallFont = FontHelper.tipBodyFont;
    private static final float QUEST_SCALE = 0.9f;

    private static final Hitbox titleHb;
    private static final Texture dropdownArrow = TexLoader.getTexture(makeUIPath("arrow.png"));

    static {
        float width = FontHelper.getWidth(largeFont, TEXT[0], 1.1f) + 35;
        titleHb = new Hitbox(width, 32);
    }

    public static void update(float xPos, float yPos) {
        titleHb.translate(xPos - titleHb.width, yPos - titleHb.height);
        titleHb.update();
    }

    public static void render(SpriteBatch sb, float xPos, float yPos) {
        //can be assumed player is not null
        List<AbstractQuest> quests = QuestManager.quests();

        /*if (Settings.lineBreakViaCharacter) {
            renderCN(sb, xPos, yPos);
            return;
        }*/

        sb.setColor(Color.WHITE);
        largeFont.getData().setScale(1.1f);

        FontHelper.renderFontRightAligned(sb, largeFont, TEXT[0], xPos, yPos - LARGE_SPACING * 0.5f, titleHb.hovered ? Color.WHITE : Settings.GOLD_COLOR);
        sb.draw(dropdownArrow, xPos - titleHb.width, yPos - 33, 32, 32);

        largeFont.getData().setScale(QUEST_SCALE);

        for (AbstractQuest quest : quests) {
            yPos -= LARGE_SPACING;
            float rewardOffset = 50; //arbitrary temp
            FontHelper.renderFontRightAligned(sb, largeFont, quest.name, xPos - rewardOffset, yPos - SMALL_SPACING * 0.5f, quest.complete() ? Settings.GOLD_COLOR : Color.WHITE);

            for (AbstractQuest.Tracker tracker : quest.trackers) {
                if (tracker.hidden()) continue;

                yPos -= SMALL_SPACING;
                Color textColor = Color.WHITE;
                if (tracker.isFailed()) {
                    textColor = Settings.RED_TEXT_COLOR;
                }
                else if (tracker.isComplete()) {
                    textColor = Settings.GOLD_COLOR;
                }
                FontHelper.renderFontRightAligned(sb, smallFont, tracker.toString(), xPos, yPos - SMALL_SPACING * 0.5f, textColor);
            }
        }

        largeFont.getData().setScale(1);


        titleHb.render(sb);
    }

    private static void renderCN(SpriteBatch sb, float xPos, float yPos) {
        //probably need different logic
    }
}
