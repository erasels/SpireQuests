package spireQuests.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class SimpleTextureLerp extends AbstractGameEffect {
    private final Texture img;
    private final float maxDuration;
    private final float sX;
    private final float sY;
    private final float sA;
    private final float sS;
    private float sO; //START x/y/angle/scale/opacity
    private float cX, cY, cA, cS, cO; //CURRENT
    private final float tX;
    private final float tY;
    private final float tA;
    private final float tS;
    private final float tO; //TARGET
    private boolean earlyEndFade = false;
    private float sET, tET = -1; //(start/target end time) if early fade triggers, lerp opacity from current time to new end time

    public SimpleTextureLerp(Texture img, float startX, float startY, float startAngle, float startScale, float startOpacity,
                             float targetX, float targetY, float targetAngle, float targetScale, float targetOpacity, float duration) {
        this.duration = 0;
        this.maxDuration = duration;
        this.img = img;
        sX = cX = startX;
        sY = cY = startY;
        sA = cA = startAngle;
        sS = cS = startScale;
        sO = cO = startOpacity;
        tX = targetX;
        tY = targetY;
        tA = targetAngle;
        tS = targetScale;
        tO = targetOpacity;
        color = Color.WHITE.cpy();
    }

    @Override
    public void update() {
        duration += Gdx.graphics.getDeltaTime();
        cX = Interpolation.linear.apply(sX, tX, duration / maxDuration);
        cY = Interpolation.linear.apply(sY, tY, duration / maxDuration);
        cA = Interpolation.linear.apply(sA, tA, duration / maxDuration);
        cS = Interpolation.linear.apply(sS, tS, duration / maxDuration);
        if (earlyEndFade) {
            cO = Interpolation.linear.apply(sO, tO, (duration - sET) / (tET - sET));
        } else {
            cO = Interpolation.linear.apply(sO, tO, duration / maxDuration);
        }

        if (duration > maxDuration) {
            isDone = true;
        } else if (duration > tET && earlyEndFade) { //early end
            isDone = true;
        }
    }

    public void earlyEnd() {
        earlyEndFade = true;
        sET = duration;
        tET = duration + 1f;
        sO = cO;
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(new Color(1, 1, 1, cO));
        float imageWidth = img.getWidth() * Settings.scale, imageHeight = img.getHeight() * Settings.scale;
        sb.draw(
                img,
                cX - imageWidth / 2f,
                cY - imageHeight / 2f,
                imageWidth / 2f,
                imageHeight / 2f,
                imageWidth,
                imageHeight,
                cS,
                cS,
                cA,
                0,
                0,
                img.getWidth(),
                img.getHeight(),
                false,
                false
        );
    }

    @Override
    public void dispose() {
    }
}
