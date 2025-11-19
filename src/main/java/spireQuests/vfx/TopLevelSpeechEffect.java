package spireQuests.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.ui.DialogWord;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.SpeechTextEffect;

public class TopLevelSpeechEffect extends AbstractGameEffect {
    private static final int RAW_W = 512;
    private static final float SHADOW_OFFSET = 16f * Settings.scale;
    private static final float WAVY_DISTANCE = 2f * Settings.scale;
    private static final float ADJUST_X = 170f * Settings.scale;
    private static final float ADJUST_Y = 116f * Settings.scale;
    private static final float FADE_TIME = 0.3f;

    private float shadow_offset = 0f;
    private final float x;
    private final float y;
    private float wavy_y, wavyHelper;
    private float scaleTimer = 0.3f;
    private final boolean facingRight;
    private final Color shadowColor = new Color(0f, 0f, 0f, 0f);

    private final SpeechTextEffect textEffect;

    public TopLevelSpeechEffect(float x, float y, String msg, boolean isPlayer) {
        this(x, y, 2f, msg, isPlayer);
    }

    public TopLevelSpeechEffect(float x, float y, float duration, String msg, boolean isPlayer) {
        this(x, y, 2f, msg, isPlayer, new Color(0.8f, 0.9f, 0.9f, 0f));
    }

    public TopLevelSpeechEffect(float x, float y, float duration, String msg, boolean isPlayer, Color color) {
        float effect_x = -170f * Settings.scale;
        if (isPlayer) {
            effect_x = 170f * Settings.scale;
        }

        textEffect = new SpeechTextEffect(x + effect_x, y + 124f * Settings.scale, duration, msg, DialogWord.AppearEffect.BUMP_IN);

        AbstractDungeon.topLevelEffectsQueue.add(textEffect);

        if (isPlayer) {
            this.x = x + ADJUST_X;
        } else {
            this.x = x - ADJUST_X;
        }
        this.y = y + ADJUST_Y;
        this.color = color;
        this.duration = duration;
        this.facingRight = !isPlayer;
    }

    public void update() {
        updateScale();

        wavyHelper += Gdx.graphics.getDeltaTime() * 4f;
        wavy_y = MathUtils.sin(wavyHelper) * WAVY_DISTANCE;

        duration -= Gdx.graphics.getDeltaTime();
        if (duration < 0f) {
            isDone = true;
        }

        if (duration > FADE_TIME) {
            color.a = MathUtils.lerp(color.a, 1f, Gdx.graphics.getDeltaTime() * 12f);
        } else {
            color.a = MathUtils.lerp(color.a, 0f, Gdx.graphics.getDeltaTime() * 12f);
        }

        if (duration < FADE_TIME / 2) {
            textEffect.duration = -1;
        }

        shadow_offset = MathUtils.lerp(shadow_offset, SHADOW_OFFSET, Gdx.graphics.getDeltaTime() * 4f);
    }

    private void updateScale() {
        scaleTimer -= Gdx.graphics.getDeltaTime();
        if (scaleTimer < 0f) {
            scaleTimer = 0f;
        }
        scale = Interpolation.swingIn.apply(Settings.scale, Settings.scale / 2f, scaleTimer / 0.3f);
    }

    @Override
    public void render(SpriteBatch sb) {
        shadowColor.a = color.a / 4f;
        sb.setColor(shadowColor);
        sb.draw(
                ImageMaster.SPEECH_BUBBLE_IMG,
                x - RAW_W / 2f + shadow_offset,
                y - RAW_W / 2f + wavy_y - shadow_offset,
                RAW_W / 2f,
                RAW_W / 2f,
                RAW_W,
                RAW_W,
                scale,
                scale,
                rotation,
                0,
                0,
                RAW_W,
                RAW_W,
                facingRight,
                false);

        sb.setColor(color);
        sb.draw(
                ImageMaster.SPEECH_BUBBLE_IMG,
                x - RAW_W / 2f,
                y - RAW_W / 2f + wavy_y,
                RAW_W / 2f,
                RAW_W / 2f,
                RAW_W,
                RAW_W,
                scale,
                scale,
                rotation,
                0,
                0,
                RAW_W,
                RAW_W,
                facingRight,
                false);
    }

    @Override
    public void dispose() {
    }

}
