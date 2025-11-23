package spireQuests.quests.gk.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.IronWaveParticle;

public class MonsterIronWaveEffect extends AbstractGameEffect {
    private float waveTimer = 0f;
    private float x, y, cX;
    private static final float WAVE_INTERVAL = 0.03f;

    /**
     * @param x  start X (typically monster.hb.cX)
     * @param y  start Y (typically monster.hb.cY)
     * @param cX target X (typically player.hb.cX)
     */
    public MonsterIronWaveEffect(float x, float y, float cX) {
        this.x = x - 120f * Settings.scale;
        this.y = y - 20f * Settings.scale;
        this.cX = cX;
    }

    @Override
    public void update() {
        waveTimer -= Gdx.graphics.getDeltaTime();
        if (waveTimer < 0f) {
            waveTimer = WAVE_INTERVAL;

            x -= 160f * Settings.scale;
            y -= 15f * Settings.scale;

            AbstractDungeon.effectsQueue.add(new IronWaveParticle(x, y));

            if (x < cX) {
                isDone = true;
                CardCrawlGame.sound.playA("ATTACK_DAGGER_6", -0.3f);
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {}

    @Override
    public void dispose() {
    }
}

