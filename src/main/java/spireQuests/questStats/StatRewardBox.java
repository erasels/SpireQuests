package spireQuests.questStats;

import static spireQuests.Anniv8Mod.makeID;
import static spireQuests.Anniv8Mod.makeUIPath;

import java.lang.reflect.Field;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.IUIElement;
import spireQuests.quests.AbstractQuest;
import spireQuests.quests.QuestReward;
import spireQuests.quests.QuestReward.CardReward;
import spireQuests.quests.QuestReward.GoldReward;
import spireQuests.quests.QuestReward.MaxHPReward;
import spireQuests.quests.QuestReward.PotionReward;
import spireQuests.quests.QuestReward.RandomRelicReward;
import spireQuests.quests.QuestReward.RelicReward;
import spireQuests.util.ImageHelper;
import spireQuests.util.TexLoader;

public class StatRewardBox implements IUIElement {
    private static final Texture CUSTOM_REWARD_IMG = ImageMaster.loadImage("images/ui/run_mods/shiny.png");
    private static final Texture FRAME = TexLoader.getTexture(makeUIPath("stats/reward_frame.png"));
    private static final Texture FRAME_HOVER = TexLoader.getTexture(makeUIPath("stats/reward_frame_hover.png"));
    private static final Texture CARD_BG_TEX = TexLoader.getTexture(makeUIPath("stats/card.png"));
    private static final Texture CARD_FG_TEX = TexLoader.getTexture(makeUIPath("stats/card_fg.png"));
    private static final Texture POTION_TEX = TexLoader.getTexture(makeUIPath("stats/potion.png"));
    private static final Texture RANDOM_RELIC_TEX = TexLoader.getTexture(makeUIPath("stats/relic.png"));
    private static final Texture GOLD_TEX = TexLoader.getTexture(makeUIPath("stats/gold.png"));
    private static final Texture HEALTH_TEX = TexLoader.getTexture(makeUIPath("stats/heart.png"));
    private static final Texture RANDOM_REWARD_TEX = TexLoader.getTexture(makeUIPath("stats/random_item.png"));

    private static final String ID = makeID(StatRewardBox.class.getSimpleName());
    private UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);

    public static final float FRAME_X = 125.0F * Settings.scale;
    public static final float FRAME_Y = 125.0F * Settings.scale;
    public static float HEIGHT = 100.0F * Settings.scale;
    public static float WIDTH = 100.0F * Settings.scale;
    public Hitbox hb;
    public TextureRegion img;
    private String header = "";
    private String body = "";
    private float xPos;
    private float yPos;
    private AbstractCard card = null;
    private AbstractPotion potion = null;
    private AbstractRelic relic = null;

    public StatRewardBox(QuestReward reward, float xPos, float yPos) {
        this(xPos, yPos);
        this.img = reward.icon();

        this.header = uiStrings.TEXT[2];
        this.body = reward.rewardText;
        if (reward instanceof RelicReward) {
            this.relic = ((RelicReward)reward).getRelic().makeCopy();
            this.header = relic.name;
            this.body = relic.description;
        }
        if (reward instanceof CardReward) {
            this.card = ((CardReward)reward).getCard().makeStatEquivalentCopy();
        }
        if (reward instanceof PotionReward) {
            this.potion = ((PotionReward)reward).getPotion().makeCopy();
            this.header = potion.name;
            this.body = potion.description;
        }
        if (reward instanceof RandomRelicReward) {
            this.header = uiStrings.TEXT[0];
            this.img = new TextureRegion(RANDOM_RELIC_TEX);
        }
        if (reward instanceof GoldReward) {
            this.header = uiStrings.TEXT[1];
            this.img = new TextureRegion(GOLD_TEX);
        }
        if (reward instanceof MaxHPReward) {
            this.header = uiStrings.TEXT[3];
            this.img = new TextureRegion(HEALTH_TEX);
        }

    }

    public StatRewardBox(AbstractQuest q, float xPos, float yPos) {
        this(xPos, yPos);
        if (q.useDefaultReward) {
            this.img = new TextureRegion(RANDOM_REWARD_TEX);
            this.header = uiStrings.TEXT[4];
            this.body = uiStrings.TEXT[5];
        } else {
            this.img = new TextureRegion(CUSTOM_REWARD_IMG);
            this.header = uiStrings.TEXT[2];
            this.body = q.getRewardsText();
        }
    }

    private StatRewardBox(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.hb = new Hitbox(xPos, yPos, FRAME_X, FRAME_Y);
    }

    public StatRewardBox(QuestReward reward) {
        this(reward, 0.0F, 0.0F);
    }

    public StatRewardBox(AbstractQuest q) {
        this(q, 0.0F, 0.0F);
    }

    public void move(float xPos, float yPos){
        this.xPos = xPos;
        this.yPos = yPos;
        this.hb.translate(xPos, yPos);
    }

    public void render(SpriteBatch sb) {
        if (this.hb.hovered) {
            sb.draw(FRAME_HOVER, xPos, yPos, FRAME_X, FRAME_Y);            
        } else {
            sb.draw(FRAME, xPos, yPos, FRAME_X, FRAME_Y);
        }

        if (this.card != null) {
            Color cardBGColor;
            switch (this.card.color) {
                case RED:
                    cardBGColor = Color.SCARLET;
                    break;
                case GREEN:
                    cardBGColor = Color.CHARTREUSE;
                    break;
                case BLUE:
                    cardBGColor = Color.SKY;
                    break;
                case PURPLE:
                    cardBGColor = Settings.PURPLE_COLOR;
                    break;
                default:
                    cardBGColor = Color.WHITE;
                    break;
            }
            sb.setColor(cardBGColor);
            this.img = new TextureRegion(CARD_BG_TEX);
            sb.draw(this.img, xPos + (FRAME_X - WIDTH) / 2, yPos + (FRAME_Y - HEIGHT) / 2, WIDTH, HEIGHT);
            sb.setColor(Color.WHITE);
            this.img = new TextureRegion(CARD_FG_TEX);
            sb.draw(this.img, xPos + (FRAME_X - WIDTH) / 2, yPos + (FRAME_Y - HEIGHT) / 2, WIDTH, HEIGHT);
        } else if (this.potion != null) {
            try {
                renderPotion(sb);
            } catch (Exception e) {
                this.img = new TextureRegion(POTION_TEX);
                sb.draw(this.img, xPos + (FRAME_X - WIDTH) / 2, yPos + (FRAME_Y - HEIGHT) / 2, WIDTH, HEIGHT);
            }
        } else {
            sb.draw(this.img, xPos + (FRAME_X - WIDTH) / 2, yPos + (FRAME_Y - HEIGHT) / 2, WIDTH, HEIGHT);
        }

        if (this.hb.hovered) {
            if (card != null) {
                card.current_x = InputHelper.mX + (AbstractCard.RAW_W * card.drawScale) * Settings.scale;
                card.current_y = InputHelper.mY;
                card.renderHoverShadow(sb);
                card.render(sb);
            } else {
               ImageHelper.tipBoxAtMousePos(this.header, this.body);
            }
        }
        this.hb.render(sb);
     }

    private void renderPotion(SpriteBatch sb) throws 
        NoSuchFieldException,
        SecurityException,
        IllegalArgumentException,
        IllegalAccessException 
    {
        Field containerImg = AbstractPotion.class.getDeclaredField("containerImg");
        Field outlineImg = AbstractPotion.class.getDeclaredField("outlineImg");
        Field liquidImg = AbstractPotion.class.getDeclaredField("liquidImg");
        Field hybridImg = AbstractPotion.class.getDeclaredField("hybridImg");
        Field spotsImg = AbstractPotion.class.getDeclaredField("spotsImg");

        containerImg.setAccessible(true);
        outlineImg.setAccessible(true);
        liquidImg.setAccessible(true);
        hybridImg.setAccessible(true);
        spotsImg.setAccessible(true);

        Texture potionTex;
        if (this.potion.liquidColor != null) {
            potionTex = (Texture) liquidImg.get(this.potion);
            sb.setColor(this.potion.liquidColor);
            sb.draw(potionTex, xPos + (FRAME_X - WIDTH) / 2, yPos + (FRAME_Y - HEIGHT) / 2, WIDTH, HEIGHT);
        }
        if (this.potion.hybridColor != null) {
            potionTex = (Texture) hybridImg.get(this.potion);
            sb.setColor(this.potion.hybridColor);
            sb.draw(potionTex, xPos + (FRAME_X - WIDTH) / 2, yPos + (FRAME_Y - HEIGHT) / 2, WIDTH, HEIGHT);
        }
        if (this.potion.spotsColor != null) {
            potionTex = (Texture) spotsImg.get(this.potion);
            sb.setColor(this.potion.spotsColor);
            sb.draw(potionTex, xPos + (FRAME_X - WIDTH) / 2, yPos + (FRAME_Y - HEIGHT) / 2, WIDTH, HEIGHT);
        }
        
        sb.setColor(Color.WHITE);
        potionTex = (Texture) containerImg.get(this.potion);
        sb.draw(potionTex, xPos + (FRAME_X - WIDTH) / 2, yPos + (FRAME_Y - HEIGHT) / 2, WIDTH, HEIGHT);
    }

    public void update() {
        this.hb.update();
        if (this.hb.justHovered) {
            CardCrawlGame.sound.play("UI_HOVER");
        }
        if (this.hb.hovered && InputHelper.justClickedRight) {
            if (card != null) {
                CardCrawlGame.cardPopup.open(card);
            } else if (relic != null) {
                CardCrawlGame.relicPopup.open(relic);
            }
        }
    }

    @Override
    public int renderLayer() {
        return 1;
    }

    @Override
    public int updateOrder() {
        return 1;
    }
}