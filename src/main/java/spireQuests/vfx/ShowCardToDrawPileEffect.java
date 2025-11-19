package spireQuests.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;

public class ShowCardToDrawPileEffect
        extends AbstractGameEffect {
    private static final float EFFECT_DUR = 1.5F;
    private final AbstractCard card;
    private static final float PADDING = 30.0F * Settings.scale;
    private boolean cardOffset = false;

    public ShowCardToDrawPileEffect(AbstractCard srcCard, float x, float y, boolean cardOffset) {
        this.card = srcCard.makeStatEquivalentCopy();
        this.cardOffset = cardOffset;
        this.duration = 1.5F;

        if (cardOffset) {
            identifySpawnLocation(x, y);
        } else {
            this.card.target_x = x;
            this.card.target_y = y;
        }

        AbstractDungeon.effectsQueue.add(new CardPoofEffect(this.card.target_x, this.card.target_y));
        this.card.drawScale = 0.01F;
        this.card.targetDrawScale = 1.0F;
        CardCrawlGame.sound.play("CARD_OBTAIN");
    }

    public ShowCardToDrawPileEffect(AbstractCard srcCard) {
        this.card = srcCard.makeStatEquivalentCopy();
        this.duration = 1.5F;
        this.card.target_x = MathUtils.random(Settings.WIDTH * 0.1F, Settings.WIDTH * 0.9F);
        this.card.target_y = MathUtils.random(Settings.HEIGHT * 0.8F, Settings.HEIGHT * 0.2F);
        AbstractDungeon.effectsQueue.add(new CardPoofEffect(this.card.target_x, this.card.target_y));
        this.card.drawScale = 0.01F;
        this.card.targetDrawScale = 1.0F;
    }


    private void identifySpawnLocation(float x, float y) {
        int effectCount = 0;
        if (this.cardOffset) {
            effectCount = 1;
        }
        for (AbstractGameEffect e : AbstractDungeon.effectList) {
            if (e instanceof ShowCardToDrawPileEffect) {
                effectCount++;
            }
        }

        this.card.current_x = x;
        this.card.current_y = y;
        this.card.target_y = Settings.HEIGHT * 0.5F;

        switch (effectCount) {
            case 0:
                this.card.target_x = Settings.WIDTH * 0.5F;
                return;
            case 1:
                this.card.target_x = Settings.WIDTH * 0.5F - PADDING - AbstractCard.IMG_WIDTH;
                return;
            case 2:
                this.card.target_x = Settings.WIDTH * 0.5F + PADDING + AbstractCard.IMG_WIDTH;
                return;
            case 3:
                this.card.target_x = Settings.WIDTH * 0.5F - (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;
                return;
            case 4:
                this.card.target_x = Settings.WIDTH * 0.5F + (PADDING + AbstractCard.IMG_WIDTH) * 2.0F;
                return;
        }
        this.card.target_x = MathUtils.random(Settings.WIDTH * 0.1F, Settings.WIDTH * 0.9F);
        this.card.target_y = MathUtils.random(Settings.HEIGHT * 0.2F, Settings.HEIGHT * 0.8F);
    }


    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        this.card.update();

        if (this.duration < 0.0F) {
            this.isDone = true;
            this.card.shrink();
            (AbstractDungeon.getCurrRoom()).souls.onToDeck(this.card, false, true);
        }
    }


    public void render(SpriteBatch sb) {
        if (!this.isDone)
            this.card.render(sb);
    }

    public void dispose() {
    }
}
