package spireQuests.quests.gk.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class FakePlayCardEffect extends AbstractGameEffect {
    private static final float START_SCALE = 0.75f, END_SCALE = 0.15f;
    private static final float POPIN = 0.25f;

    private AbstractCard card;
    private AbstractCreature playSource;

    // Cache the starting position for the card
    private float startX;
    private float startY;

    public FakePlayCardEffect(AbstractCreature playSource, AbstractCard toShow) {
        this.playSource = playSource;
        this.card = toShow.makeStatEquivalentCopy();
        this.startingDuration = this.duration = 1.75f; // 1.75 seconds, not using Settings.DUR so fast mode doesn't affect it
    }

    @Override
    public void update() {
        if (startingDuration == duration) {
            // Decide which side of the screen to display the card on
            int displayPosition = Settings.WIDTH / 2;
            if (playSource.hb_x > displayPosition) {
                displayPosition *= 0.9f;
            } else {
                displayPosition *= 1.1f;
            }

            // Initial card position and scale (before it starts moving)
            startX = displayPosition;
            startY = Settings.HEIGHT * 0.5f;

            card.current_x = startX;
            card.current_y = startY;
            card.target_x = startX;
            card.target_y = startY;
            card.drawScale = START_SCALE;
            card.targetDrawScale = START_SCALE;
            card.angle = 0.0f;
        }

        if (duration > startingDuration - POPIN) {
            float t0 = (startingDuration - duration) / POPIN;
            t0 = MathUtils.clamp(t0, 0f, 1f);

            card.drawScale = MathUtils.lerp(END_SCALE, START_SCALE, t0);

            card.current_x = startX;
            card.current_y = startY;
            card.angle = 0f;
        } else
        if (duration <= startingDuration / 2f) {
            // Normalized time from 0 to 1 over the second half of the effect
            float half = startingDuration / 2f;
            float t = (half - duration) / half;
            t = MathUtils.clamp(t, 0.0f, 1.0f);

            // Lerp the scale from 100% to 25%
            float alpha = MathUtils.clamp(t * 2f, 0f, 1f);
            float scale = MathUtils.lerp(START_SCALE, END_SCALE, alpha);
            card.drawScale = scale;
            card.transparency = card.targetTransparency = MathUtils.lerp(1, 0, t);

            // Start and end positions (center of source hitbox)
            float sx = startX;
            float sy = startY;
            float ex = playSource.hb.cX;
            float ey = playSource.hb.cY;

            // Control point for the arc
            float cx = (sx + ex) / 2f;
            float cy = Math.min(sy, ey) - 100f * Settings.scale;

            // Math shit, Bezier curve or something
            float u = 1.0f - t;
            card.current_x = u * u * sx + 2f * u * t * cx + t * t * ex;
            card.current_y = u * u * sy + 2f * u * t * cy + t * t * ey;

            // Make card spin
            card.angle = 720f * t;
        } else {
            // First half: show card for half the duration
            card.current_x = startX;
            card.current_y = startY;
            card.drawScale = START_SCALE;
            card.angle = 0.0f;
        }

        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 0.0F) {
            isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (!isDone) {
            card.update();
            card.render(spriteBatch);
        }
    }

    @Override
    public void dispose() {}
}