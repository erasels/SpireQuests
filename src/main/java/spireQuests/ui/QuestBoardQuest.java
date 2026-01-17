package spireQuests.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import spireQuests.Anniv8Mod;
import spireQuests.questStats.QuestStats;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestManager;
import spireQuests.util.TexLoader;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.Color.WHITE;
import static spireQuests.Anniv8Mod.makeUIPath;

public class QuestBoardQuest {
    public static final String ID = spireQuests.Anniv8Mod.makeID("QuestBoard");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    public AbstractQuest quest;
    public QuestStats questStats;
    private final float x;
    private final float y;
    private final Hitbox hb; // hitbox for the pickup button
    private final Hitbox previewHb; // hitbox for showing the previews on hover
    public boolean taken;
    protected Color lockAlpha = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private ArrayList<PowerTip> tips = new ArrayList<>();

    public QuestBoardQuest(AbstractQuest quest, float x, float y) {
        this.quest = quest;
        this.questStats = new QuestStats(quest);
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(300.0F * Settings.xScale, 64.0F * Settings.yScale);
        this.previewHb = new Hitbox(512.0F * Settings.xScale, 716.0F * Settings.yScale);

        this.tips = new ArrayList<>(quest.getPreviewTips());
        if (Anniv8Mod.trophyTooltipsEnabled()){
            tips.add(this.questStats.trophyTip);
        }
    }

    public void render(SpriteBatch sb, float boardY) {
        if (!taken) {
            // Render bg
            sb.setColor(Color.WHITE.cpy());
            sb.draw(ImageMaster.REWARD_SCREEN_SHEET, this.x, this.y - 350.0F * Settings.yScale + boardY, 512.0F * Settings.xScale, 716.0F * Settings.yScale);
            sb.draw(ImageMaster.VICTORY_BANNER, this.x - 50.0F * Settings.xScale, this.y + 199.0F * Settings.yScale + boardY, 612.0F * Settings.xScale, 238.0F * Settings.yScale);

            // Quest name
            FontHelper.renderFontCentered(sb, FontHelper.losePowerFont, this.quest.name, this.x + 260.0F * Settings.xScale, this.y + 340.0F * Settings.yScale + boardY, Color.WHITE, quest.getTitleScale());
            FontHelper.renderSmartText(
                    sb,
                    FontHelper.cardDescFont_N,
                    TEXT[4] + quest.author,
                    this.x + 55F * Settings.xScale,
                    this.y + 235F * Settings.yScale + boardY,
                    Settings.CREAM_COLOR
            );

            // Hitboxes
            this.hb.move(this.x + 5.0F * Settings.xScale + (512.0F / 2) * Settings.xScale, this.y - 445.0F * Settings.yScale + boardY + (256.0F / 2) * Settings.yScale);
            this.previewHb.move(this.x + (512.0F / 2) * Settings.xScale, this.y - 350.0F * Settings.yScale + boardY + (716.0F / 2) * Settings.yScale);
            this.hb.render(sb);
            this.previewHb.render(sb);

            // Take quest btn
            if (!QuestManager.canObtainQuests()) {
                sb.setColor(Color.GRAY.cpy());
            } else if (this.hb.hovered) {
                sb.setColor(Color.GOLD.cpy());
            }
            sb.draw(ImageMaster.REWARD_SCREEN_TAKE_BUTTON, this.x + 5.0F * Settings.xScale, this.y - 445.0F * Settings.yScale + boardY, 512.0F * Settings.xScale, 256.0F * Settings.yScale);
            sb.setColor(Color.WHITE.cpy());
            FontHelper.renderFontCentered(sb, FontHelper.buttonLabelFont, TEXT[1], this.x + 260.0F * Settings.xScale, this.y - 315.0F * Settings.yScale + boardY, Color.WHITE, 0.8F);

            // Description and reward text
            float textWidth = (512.0F - 55.0F - 55.0F) * Settings.xScale;
            float baseLineHeight = FontHelper.getHeight(FontHelper.cardDescFont_N, "gl0!", 1f);
            float lineSpacing = baseLineHeight * 1.5f;

            FontHelper.renderSmartText(
                    sb,
                    FontHelper.cardDescFont_N,
                    TEXT[6] + quest.getDescription(),
                    this.x + 55.0F * Settings.xScale,
                    this.y + 165.0F * Settings.yScale + boardY,
                    textWidth,
                    lineSpacing,
                    Settings.CREAM_COLOR
            );

            FontHelper.renderSmartText(
                    sb,
                    FontHelper.cardDescFont_N,
                    TEXT[7] + quest.getRewardsText(),
                    this.x + 55.0F * Settings.xScale,
                    this.y - 60.0F * Settings.yScale + boardY,
                    textWidth,
                    lineSpacing,
                    Settings.CREAM_COLOR
            );

            if (Anniv8Mod.questsHaveCost()) {
                renderPrice(sb, boardY);
            }
            if (Anniv8Mod.trophyTooltipsEnabled()) {
                renderTrophy(sb, boardY);
            }
            if (this.previewHb.hovered) {
                renderPreviews(boardY);
            }
        }
    }

    private void renderTrophy(SpriteBatch sb, float boardY) {
        Texture medalIcon = this.questStats.getSmallTrophyTexture();

        sb.draw(medalIcon, this.x + (260.0F * Settings.xScale) - (72.0F / 2.0F * Settings.scale), this.y - 426.0F * Settings.yScale + boardY, 72.0F * Settings.scale, 72.0F * Settings.scale);
    }

    public void renderPreviews(float boardY) {
        float TIP_X_THRESHOLD = 1544.0F * Settings.xScale;
        float TIP_OFFSET_R_X = 20.0F * Settings.xScale;
        float TIP_OFFSET_L_X = -380.0F * Settings.xScale;
        boolean hasManyTips = this.tips.size() > 3;

        if (!this.tips.isEmpty()) {
            if (this.previewHb.cX + this.previewHb.width / 2.0F < TIP_X_THRESHOLD) {
                TipHelper.queuePowerTips(this.previewHb.cX + this.previewHb.width / 2.0F + TIP_OFFSET_R_X, this.previewHb.cY + TipHelper.calculateAdditionalOffset(this.tips, this.previewHb.cY) + boardY, tips);
            } else {
                float tipX = TIP_OFFSET_L_X + (hasManyTips ? this.tips.size() * -150.0F * Settings.xScale : 0.0F);
                TipHelper.queuePowerTips(this.previewHb.cX - this.previewHb.width / 2.0F + tipX, this.previewHb.cY + TipHelper.calculateAdditionalOffset(this.tips, this.previewHb.cY) + boardY, tips);
            }
        }
    }

    private void renderPrice(SpriteBatch sb, float boardY) {
        Color fontColor = WHITE.cpy();
        sb.setColor(lockAlpha);

        if (quest.usingGoldCost) {
            sb.draw(ImageMaster.UI_GOLD, x + 240.0F * Settings.xScale, y - 415.0F * Settings.yScale + boardY, ImageMaster.UI_GOLD.getWidth(), ImageMaster.UI_GOLD.getHeight());
        } else {
            sb.draw(ImageMaster.TP_HP, x + 235.0F * Settings.xScale, y - 420.0F * Settings.yScale + boardY, ImageMaster.TP_HP.getWidth(), ImageMaster.TP_HP.getHeight());
        }

        if (!this.canBuy()) {
            fontColor = Color.RED.cpy();
        }

        fontColor.a = lockAlpha.a;

        FontHelper.cardTitleFont.getData().setScale(1.0f);
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, "" + quest.getCost(), x + 235.0F * Settings.xScale, y - 375.0F * Settings.yScale + boardY, fontColor);
    }

    public boolean canBuy() {
        if (quest.usingGoldCost) {
            return AbstractDungeon.player.gold >= quest.getCost();
        } else {
            return true; // let the player kill themselves in case they have fairy or something idk
        }
    }

    public void update() {
        if (!taken) {
            this.hb.update();
            this.previewHb.update();
            if (QuestManager.canObtainQuests()) {
                if (this.hb.justHovered) {
                    CardCrawlGame.sound.playV("UI_HOVER", 0.75F);
                }
                if ((this.hb.hovered && InputHelper.justClickedLeft || CInputActionSet.select.isJustPressed()) && !AbstractDungeon.isScreenUp && !AbstractDungeon.isFadingOut && !AbstractDungeon.player.viewingRelics) {
                    if (!Anniv8Mod.questsHaveCost()) {
                        obtainQuest();
                    } else if (canBuy()) {
                        obtainQuest();
                        payPrice();
                    }
                }
            }
        }
    }

    private void obtainQuest() {
        CardCrawlGame.sound.play("SHOP_PURCHASE", 0.1F);
        QuestManager.startQuest(quest);
        QuestBoardScreen.parentProp.quests.remove(quest);
        QuestBoardScreen.parentProp.numQuestsPickable--;
        taken = true;
        this.hb.hovered = false;
    }

    private void payPrice() {
        if (quest.usingGoldCost) {
            AbstractDungeon.player.loseGold(quest.getCost());
        } else {
            CardCrawlGame.sound.play("BLUNT_FAST");
            AbstractDungeon.player.damage(new DamageInfo(null, quest.getCost()));
        }
    }
}
