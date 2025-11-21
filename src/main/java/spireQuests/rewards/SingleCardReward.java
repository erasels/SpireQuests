package spireQuests.rewards;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import spireQuests.Anniv8Mod;
import spireQuests.patches.FontCreationPatches;
import spireQuests.util.Wiz;

import java.util.Arrays;

import static spireQuests.rewards.RewardEnums.SQ_SINGLECARDREWARD;

public class SingleCardReward extends CustomReward {
    protected static final float REWARD_X_POS = Settings.WIDTH * 0.434F;

    private static final String prefixText = CardCrawlGame.languagePack.getUIString(Anniv8Mod.makeID("SingleCardReward")).TEXT[0];
    private static final String tipText = CardCrawlGame.languagePack.getUIString(Anniv8Mod.makeID("SingleCardReward")).TEXT[1];
    private static final Color TIP_COL = Color.WHITE.cpy();
    private static final float XOFFSET = 25f * Settings.scale;
    public AbstractCard card;
    protected AbstractCard renderCard;

    public SingleCardReward(AbstractCard c) {
        super((Texture) null, "", SQ_SINGLECARDREWARD);
        card = c;
        init();
    }

    public SingleCardReward(String... args) {
        super((Texture) null, "", SQ_SINGLECARDREWARD);

        //Loading save
        if (args.length == 1 && args[0].contains("|")) {
            String[] params = args[0].split("\\|");
            if (params.length < 3) {
                card = CardLibrary.getCopy(params[0], 0, 0);
            } else {
                card = CardLibrary.getCopy(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]));
            }
        } else {
            //Initializing a card
            int i = 0, j = 0;
            switch (args.length) {
                case 3:
                    i = Integer.parseInt(args[2]);
                case 2:
                    j = Integer.parseInt(args[1]);
                case 1:
                    card = CardLibrary.getCopy(args[0], j, i);
                    break;
                default:
                    Anniv8Mod.logger.warn(String.format("Tried to generate a SingleCardReward with %d parameters.\nInput: %s", args.length, Arrays.toString(args)));
                    card = CardLibrary.getCopy(Madness.ID);
            }
        }
        init();
    }

    protected void init() {
        for (AbstractRelic r : Wiz.p().relics)
            r.onPreviewObtainCard(card);

        renderCard = card.makeStatEquivalentCopy();
        text = prefixText + card.name;
        TIP_COL.a = 0.65f;
    }

    @Override
    public boolean claimReward() {
        AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(card, InputHelper.mX, InputHelper.mY));
        return true;
    }

    public void update() {
        super.update();

        if (this.hb.hovered && InputHelper.justClickedRight && !this.isDone) {
            CardCrawlGame.sound.playA("UI_CLICK_1", 0.1F);
            CardCrawlGame.cardPopup.open(this.card);
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        Color col;
        if (hb.hovered) {
            sb.setColor(new Color(0.4f, 0.6f, 0.6f, 1.0f));
            col = Settings.GOLD_COLOR;
        } else {
            sb.setColor(new Color(0.5f, 0.6f, 0.6f, 0.8f));
            col = Settings.CREAM_COLOR;
        }

        if (hb.clickStarted) {
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.xScale * 0.98f, Settings.scale * 0.98f, 0.0f, 0, 0, 464, 98, false, false);
        } else {
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.xScale, Settings.scale, 0.0f, 0, 0, 464, 98, false, false);
        }

        if (this.flashTimer != 0.0f) {
            sb.setColor(0.6f, 1.0f, 1.0f, this.flashTimer * 1.5f);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0f - 232.0f, this.y - 49.0f, 232.0f, 49.0f, 464.0f, 98.0f, Settings.xScale * 1.03f, Settings.scale * 1.15f, 0.0f, 0, 0, 464, 98, false, false);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

        float scale = renderCard.drawScale;

        renderCard.drawScale = 0.175f;
        renderCard.current_x = card.target_x = hb.x + ((AbstractCard.RAW_W * renderCard.drawScale) * Settings.scale) / 2f + XOFFSET;
        renderCard.current_y = card.target_y = hb.cY;
        renderCard.render(sb);

        renderCard.drawScale = scale;

        FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, text, Settings.WIDTH * 0.434F, y + 5.0f * Settings.scale, 1000.0f * Settings.scale, 0.0f, col);
        FontHelper.renderSmartText(sb, FontCreationPatches.tipFont, tipText, REWARD_X_POS, this.y - FontHelper.getHeight(FontHelper.cardDescFont_N, text, Settings.scale) - 6f * Settings.scale, 1000.0f * Settings.scale, 0.0f, TIP_COL);


        if (hb.hovered || hb.justHovered) {
            Anniv8Mod.hoverRewardWorkaround = this;
        }

        hb.render(sb);
    }

    //Due to reward scrolling's orthographic camera and render order of rewards, the card needs to be rendered outside of the render method
    public void renderCardOnHover(SpriteBatch sb) {
        renderCard.current_x = card.target_x = InputHelper.mX + (AbstractCard.RAW_W * renderCard.drawScale) * Settings.scale;
        renderCard.current_y = card.target_y = InputHelper.mY;
        renderCard.render(sb);
    }
}

