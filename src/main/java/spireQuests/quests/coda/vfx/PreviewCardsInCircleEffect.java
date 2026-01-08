package spireQuests.quests.coda.vfx;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class PreviewCardsInCircleEffect extends AbstractGameEffect {

    private static final float CARD_SCALE = 0.5F * Settings.scale;
    private static final float SPAWN_DELAY = 0.15F;
    private static final float CONVERGE_DELAY = 2.5F;
    private static final float POST_CONVERGE_DELAY = 1.0F;

    private ArrayList<AbstractCard> cardsToPreview = new ArrayList<>();
    private float cX;
    private float cY;
    private float startAngle;
    private float angleStep;
    private float radius;

    private int cardsSpawned = 0;
    private float spawnTimer = SPAWN_DELAY;
    private float convergeTimer = CONVERGE_DELAY;
    private boolean converging = false;
    private float arc;
    
    public PreviewCardsInCircleEffect(AbstractCreature creatureToOrbit, ArrayList<AbstractCard> cards, float startAngle, float radius) {
        this(creatureToOrbit.hb.cX, creatureToOrbit.hb.cY, cards, startAngle, 360.0F, radius);
    }
    
    public PreviewCardsInCircleEffect(float x, float y, ArrayList<AbstractCard> cards, float startAngle, float radius) { 
        this(x, y, cards, startAngle, 360.0F, radius);
    }
    
    public PreviewCardsInCircleEffect(AbstractCreature creatureToOrbit, ArrayList<AbstractCard> cards, float startAngle, float arcAngle, float radius) {
        this(creatureToOrbit.hb.cX, creatureToOrbit.hb.cY, cards, startAngle, arcAngle, radius);
    }

    public PreviewCardsInCircleEffect(float x, float y, ArrayList<AbstractCard> cards, float startAngle, float arcAngle, float radius) {
        this.cX = x;
        this.cY = y;
        this.cardsToPreview = cards;
        this.radius = radius;
        this.duration = POST_CONVERGE_DELAY;
        this.isDone = false;

        this.startAngle = startAngle;
        this.arc = arcAngle;
        this.angleStep = arc / this.cardsToPreview.size();

        for (AbstractCard c : this.cardsToPreview) {
            c.drawScale = c.targetDrawScale = 0.01F;
            c.current_x = c.target_x = cX;
            c.current_y = c.target_y = cY;
        }
    }

    @Override
    public void update() {
        for (AbstractCard c : this.cardsToPreview) {
            c.update();
        }

        float dt = Gdx.graphics.getDeltaTime();

        if (this.cardsSpawned < cardsToPreview.size()) {
            spawnTimer -= dt;
            if (spawnTimer <= 0.0F) {
                placeCardInCircle(this.cardsToPreview.get(this.cardsSpawned), this.cardsSpawned);
                this.cardsSpawned++;
                this.spawnTimer = SPAWN_DELAY;
            }
        }
        else if (!converging) {
            convergeTimer -=dt;
            if (convergeTimer <= 0.0F) {
                startConvergence();
                converging = true;
            }
        } else if (converging) {
            this.duration -= dt;
        }

        if (this.duration <= 0.0F) {
            this.isDone = true;
        }
    }
    
    private void placeCardInCircle(AbstractCard c, int cardIndex) {
        CardCrawlGame.sound.play("CARD_OBTAIN"); 
        float angle = startAngle + cardIndex * angleStep;

        // Trig, hell yeah
        float x = cX + radius * MathUtils.cosDeg(angle);
        float y = cY + radius * MathUtils.sinDeg(angle);

        c.target_x = x;
        c.target_y = y;
        c.targetDrawScale = CARD_SCALE;
        // c.targetAngle = angle - 90.0F;
    }

    private void startConvergence() {
        for (AbstractCard c : this.cardsToPreview) {
            c.target_x = cX;
            c.target_y = cY;
            c.targetDrawScale = 0.01F;
            c.targetAngle = 0.0F;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!this.isDone) {
            for (AbstractCard c : this.cardsToPreview) {
                c.render(sb);
            }
        }
    }
    
    @Override
    public void dispose() {}
}
