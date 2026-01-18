package spireQuests.quests.soytheproton.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import static spireQuests.Anniv8Mod.makeContributionPath;

public class ExclamationParticle extends AbstractGameEffect {
    private static final float DURATION = 2f;
    private final Texture img;
    private final float x;
    private float y;
    private final float tY;
    private float vY;

    public ExclamationParticle(float x, float y) {
        this.duration = DURATION;
        this.img = new Texture(Gdx.files.internal(makeContributionPath("soytheproton", "ExclamationParticle.png")));
        this.x = x;
        this.tY = y + 50.0F * Settings.scale;
        this.y = y;
        this.vY = 90.0F * Settings.scale;
        color = Color.WHITE.cpy();
    }

    @Override
    public void update() {
        duration -= Gdx.graphics.getDeltaTime();
        this.y += this.vY * Gdx.graphics.getDeltaTime();
        this.vY -= this.vY * Gdx.graphics.getDeltaTime();
        if(y >= tY) this.vY = 0;
        color.a = Interpolation.fade.apply(1f, 0f, 1f - duration / DURATION) / 2f;

        if (duration < 0f) {
            isDone = true;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(color);
        float imageWidth = img.getWidth() * Settings.scale, imageHeight = img.getHeight() * Settings.scale;
        sb.draw(
                img,
                x - imageWidth / 2f,
                y - imageHeight / 2f,
                imageWidth / 2f,
                imageHeight / 2f,
                imageWidth,
                imageHeight,
                scale,
                scale,
                rotation,
                0,
                0,
                img.getWidth(),
                img.getHeight(),
                false,
                false
        );
        sb.setBlendFunction(770, 771);
    }

    @Override
    public void dispose() {
    }
}
