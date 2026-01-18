package spireQuests.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import spireQuests.Anniv8Mod;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.util.ImageHelper;
import spireQuests.util.TexLoader;

import java.util.ArrayList;
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
    private static final List<Hitbox> questHitboxes = new ArrayList<>();
    private static final Texture dropdownArrow = TexLoader.getTexture(makeUIPath("arrow.png"));
    private static final float ABANDON_TIME = 2.0F;

    public static boolean expanded = true;
    private static float dropdownAngle = 0;
    private static float questAlpha = 1;
    private static float abandonTimer = 0.0F;

    static {
        float width = FontHelper.getWidth(largeFont, TEXT[0], 1.1f) + 35;
        titleHb = new Hitbox(width, 32);
    }

    public static void update(float xPos, float yPos) {
        float currentY = yPos - titleHb.height;
        titleHb.translate(xPos - titleHb.width, currentY);
        titleHb.update();

        List<AbstractQuest> quests = QuestManager.quests();
        for (int i = 0; i < quests.size(); ++i) {
            AbstractQuest quest = quests.get(i);
            if (questHitboxes.size() <= i) questHitboxes.add(new Hitbox(1, 1));

            Hitbox hb = questHitboxes.get(i);

            int trackerCount = 0;
            for (AbstractQuest.Tracker t : quest.trackers) {
                if (!t.hidden()) ++trackerCount;
            }

            float height = LARGE_SPACING + SMALL_SPACING * trackerCount;
            currentY -= height;

            hb.resize(quest.width, height - 2);
            hb.translate(xPos - quest.width, currentY + 1);
            hb.update();

            if (hb.hovered) {
                if (Settings.isDebug && InputHelper.justClickedRight) {
                    QuestManager.failQuest(quest);
                    continue;
                }

                if (InputHelper.justClickedLeft || InputHelper.justClickedRight) {
                    if (quest.complete() || quest.fail()) {
                        QuestManager.completeQuest(quest);
                        continue;
                    }
                }

                if (AbstractDungeon.screen == QuestBoardScreen.Enum.QUEST_BOARD) {
                    if (InputHelper.isMouseDown_R && quest.isAbandoning) {
                        abandonTimer -= Gdx.graphics.getDeltaTime();
                        if (abandonTimer < 0.0F) {
                            quest.failSFX();
                            QuestManager.failQuest(quest);
                        }
                    }
                    if (InputHelper.justClickedRight) {
                        quest.isAbandoning = true;
                        abandonTimer = ABANDON_TIME;
                    }
                    if (InputHelper.justReleasedClickRight) {
                        quest.isAbandoning = false;
                    }
                } else {
                    quest.isAbandoning = false;
                }


            } else {
                quest.isAbandoning = false;
            }
        }

        while (questHitboxes.size() > quests.size()) {
            questHitboxes.remove(questHitboxes.size() - 1);
        }


        if (titleHb.hovered && InputHelper.justClickedLeft) {
            expanded = !expanded;
        }

        if (expanded) {
            dropdownAngle -= Gdx.graphics.getDeltaTime() * 360f;
            questAlpha += Gdx.graphics.getDeltaTime() * 4f;
            if (dropdownAngle < 0) dropdownAngle = 0;
            if (questAlpha > 1) questAlpha = 1;
        } else {
            dropdownAngle += Gdx.graphics.getDeltaTime() * 360f;
            questAlpha -= Gdx.graphics.getDeltaTime() * 4f;
            if (dropdownAngle > 90) dropdownAngle = 90;
            if (questAlpha < 0) questAlpha = 0;
        }
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
        sb.draw(dropdownArrow, xPos - titleHb.width, yPos - 33, 16, 16, 32, 32, 1, 1, dropdownAngle,
                0, 0, 32, 32, false, false);

        if (questAlpha > 0) {
            largeFont.getData().setScale(QUEST_SCALE);

            Settings.GOLD_COLOR.a = questAlpha;
            Settings.RED_TEXT_COLOR.a = questAlpha;
            Color.LIGHT_GRAY.a = questAlpha;
            Color.WHITE.a = questAlpha;

            for (int i = 0; i < quests.size(); ++i) {
                AbstractQuest quest = quests.get(i);
                boolean complete = quest.complete();
                boolean failed = quest.fail();

                if (questHitboxes.size() <= i) questHitboxes.add(new Hitbox(1, 1));
                Hitbox hb = questHitboxes.get(i);

                yPos -= LARGE_SPACING;
                float rewardOffset = !failed ? 34 * quest.questRewards.size() + 8 : 0;
                FontHelper.renderFontRightAligned(sb, largeFont, quest.name, xPos - rewardOffset, yPos - SMALL_SPACING * 0.5f, complete ? Settings.GOLD_COLOR : failed ? Settings.RED_TEXT_COLOR : Color.WHITE);

                quest.width = FontHelper.layout.width + rewardOffset;

                if (!failed) {
                    for (int j = 0; j < quest.questRewards.size(); ++j) {
                        sb.draw(quest.questRewards.get(j).icon(), xPos - (32 * (quest.questRewards.size() - j)), yPos - (SMALL_SPACING * 1.1f), 32, 32);
                    }
                }

                for (AbstractQuest.Tracker tracker : quest.trackers) {
                    if (tracker.hidden()) continue;

                    yPos -= SMALL_SPACING;
                    Color textColor = Color.LIGHT_GRAY;
                    if (hb.hovered) {
                        textColor = Color.WHITE;
                    } else if (tracker.isFailed()) {
                        textColor = Settings.RED_TEXT_COLOR;
                    } else if (tracker.isComplete()) {
                        textColor = Settings.GOLD_COLOR;
                    } else if (tracker.isDisabled()) {
                        textColor = Color.GRAY;
                    }

                    if (quest.isAbandoning) {
                        textColor = textColor.cpy().lerp(Settings.RED_TEXT_COLOR, Interpolation.circleIn.apply(abandonTimer % 1));
                    }
                    FontHelper.renderFontRightAligned(sb, smallFont, tracker.toString(), xPos, yPos - SMALL_SPACING * 0.5f, textColor);
                    sb.setColor(Color.WHITE);
                    quest.width = Math.max(quest.width, FontHelper.layout.width);
                }

                if (hb.hovered) {
                    if (quest.needHoverTip || Anniv8Mod.alwaysShowDescriptionEnabled() && !complete && !failed) {
                        PowerTip tooltip = quest.getHoverTooltip();
                        ImageHelper.tipBoxAtMousePos(tooltip.header, tooltip.body);
                    }
                }
            }

            Settings.GOLD_COLOR.a = 1;
            Settings.RED_TEXT_COLOR.a = 1;
            Color.LIGHT_GRAY.a = 1;
            Color.WHITE.a = 1;
            largeFont.getData().setScale(1);
        }


        titleHb.render(sb);
        if (expanded) {
            for (Hitbox hb : questHitboxes) {
                hb.render(sb);
            }
        }
    }

    private static void renderCN(SpriteBatch sb, float xPos, float yPos) {
        //probably need different logic
    }
}
